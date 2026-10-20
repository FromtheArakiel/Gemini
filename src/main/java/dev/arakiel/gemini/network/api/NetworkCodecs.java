package dev.arakiel.gemini.network.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class NetworkCodecs {
    public static final int MAX_COLLECTION_SIZE = 4096;
    public static final int MAX_BYTE_ARRAY_SIZE = 1_048_576;
    public static final int MAX_STRING_LENGTH = 32767;
    public static final StreamCodec<RegistryFriendlyByteBuf, Boolean> BOOL = StreamCodec.of(
            (buffer, value) -> buffer.writeBoolean(value),
            RegistryFriendlyByteBuf::readBoolean
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Byte> BYTE = StreamCodec.of(
            (buffer, value) -> buffer.writeByte(value),
            buffer -> buffer.readByte()
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Short> SHORT = StreamCodec.of(
            (buffer, value) -> buffer.writeShort(value),
            buffer -> buffer.readShort()
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Character> CHAR = StreamCodec.of(
            (buffer, value) -> buffer.writeChar(value),
            buffer -> buffer.readChar()
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Integer> VAR_INT = StreamCodec.of(
            (buffer, value) -> buffer.writeVarInt(value),
            RegistryFriendlyByteBuf::readVarInt
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Long> VAR_LONG = StreamCodec.of(
            (buffer, value) -> buffer.writeVarLong(value),
            RegistryFriendlyByteBuf::readVarLong
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Float> FLOAT = StreamCodec.of(
            (buffer, value) -> buffer.writeFloat(value),
            buffer -> buffer.readFloat()
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Double> DOUBLE = StreamCodec.of(
            (buffer, value) -> buffer.writeDouble(value),
            buffer -> buffer.readDouble()
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, String> STRING = string(MAX_STRING_LENGTH);

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> RESOURCE_LOCATION =
            StreamCodec.of(
                    (buffer, value) -> buffer.writeResourceLocation(value),
                    RegistryFriendlyByteBuf::readResourceLocation
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, UUID> UUID = StreamCodec.of(
            (buffer, value) -> {
                buffer.writeLong(value.getMostSignificantBits());
                buffer.writeLong(value.getLeastSignificantBits());
            },
            buffer -> new UUID(buffer.readLong(), buffer.readLong())
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC3 = StreamCodec.of(
            (buffer, value) -> {
                buffer.writeDouble(value.x);
                buffer.writeDouble(value.y);
                buffer.writeDouble(value.z);
            },
            buffer -> new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPos> BLOCK_POS = StreamCodec.of(
            (buffer, value) -> buffer.writeBlockPos(value),
            buffer -> buffer.readBlockPos()
    );

    private NetworkCodecs() {
    }

    public static StreamCodec<RegistryFriendlyByteBuf, String> string(int maxLength) {
        if (maxLength < 0 || maxLength > 1_048_576) {
            throw new IllegalArgumentException("Invalid string length limit: " + maxLength);
        }
        return StreamCodec.of(
                (buffer, value) -> buffer.writeUtf(value, maxLength),
                buffer -> buffer.readUtf(maxLength)
        );
    }

    public static StreamCodec<RegistryFriendlyByteBuf, byte[]> byteArray() {
        return byteArray(MAX_BYTE_ARRAY_SIZE);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, byte[]> byteArray(int maxLength) {
        if (maxLength < 0 || maxLength > MAX_BYTE_ARRAY_SIZE) {
            throw new IllegalArgumentException("Invalid byte array length limit: " + maxLength);
        }
        return StreamCodec.of(
                (buffer, value) -> {
                    if (value.length > maxLength) {
                        throw new IllegalArgumentException("Byte array exceeds limit: " + value.length);
                    }
                    buffer.writeVarInt(value.length);
                    buffer.writeBytes(value);
                },
                buffer -> {
                    int length = buffer.readVarInt();
                    if (length < 0 || length > maxLength) {
                        throw new IllegalArgumentException("Invalid network byte array size: " + length);
                    }
                    byte[] value = new byte[length];
                    buffer.readBytes(value);
                    return value;
                }
        );
    }

    public static StreamCodec<RegistryFriendlyByteBuf, ItemStack> itemStack() {
        return ItemStackCodecHolder.CODEC;
    }

    public static <E extends Enum<E>> StreamCodec<RegistryFriendlyByteBuf, E> enumCodec(Class<E> enumType) {
        Objects.requireNonNull(enumType, "enumType");
        E[] constants = enumType.getEnumConstants();
        if (constants == null) {
            throw new IllegalArgumentException("Not an enum: " + enumType.getName());
        }
        return StreamCodec.of(
                (buffer, value) -> buffer.writeUtf(value.name(), 64),
                buffer -> {
                    String name = buffer.readUtf(64);
                    for (E constant : constants) {
                        if (constant.name().equals(name)) {
                            return constant;
                        }
                    }
                    throw new IllegalArgumentException("Unknown enum constant " + name + " for " + enumType.getName());
                }
        );
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, List<T>> list(
            StreamCodec<RegistryFriendlyByteBuf, T> elementCodec
    ) {
        Objects.requireNonNull(elementCodec, "elementCodec");
        return StreamCodec.of(
                (buffer, values) -> {
                    validateCollectionSize(values.size());
                    buffer.writeVarInt(values.size());
                    for (T value : values) {
                        elementCodec.encode(buffer, value);
                    }
                },
                buffer -> {
                    int size = readCollectionSize(buffer);
                    List<T> values = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        values.add(elementCodec.decode(buffer));
                    }
                    return values;
                }
        );
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, Set<T>> set(
            StreamCodec<RegistryFriendlyByteBuf, T> elementCodec
    ) {
        Objects.requireNonNull(elementCodec, "elementCodec");
        return StreamCodec.of(
                (buffer, values) -> {
                    validateCollectionSize(values.size());
                    buffer.writeVarInt(values.size());
                    for (T value : values) {
                        elementCodec.encode(buffer, value);
                    }
                },
                buffer -> {
                    int size = readCollectionSize(buffer);
                    Set<T> values = new LinkedHashSet<>(size);
                    for (int i = 0; i < size; i++) {
                        values.add(elementCodec.decode(buffer));
                    }
                    return values;
                }
        );
    }

    public static <K, V> StreamCodec<RegistryFriendlyByteBuf, Map<K, V>> map(
            StreamCodec<RegistryFriendlyByteBuf, K> keyCodec,
            StreamCodec<RegistryFriendlyByteBuf, V> valueCodec
    ) {
        Objects.requireNonNull(keyCodec, "keyCodec");
        Objects.requireNonNull(valueCodec, "valueCodec");
        return StreamCodec.of(
                (buffer, values) -> {
                    validateCollectionSize(values.size());
                    buffer.writeVarInt(values.size());
                    for (Map.Entry<K, V> entry : values.entrySet()) {
                        keyCodec.encode(buffer, entry.getKey());
                        valueCodec.encode(buffer, entry.getValue());
                    }
                },
                buffer -> {
                    int size = readCollectionSize(buffer);
                    Map<K, V> values = new LinkedHashMap<>(size);
                    for (int i = 0; i < size; i++) {
                        values.put(keyCodec.decode(buffer), valueCodec.decode(buffer));
                    }
                    return values;
                }
        );
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, Optional<T>> optional(
            StreamCodec<RegistryFriendlyByteBuf, T> valueCodec
    ) {
        Objects.requireNonNull(valueCodec, "valueCodec");
        return StreamCodec.of(
                (buffer, value) -> {
                    buffer.writeBoolean(value.isPresent());
                    value.ifPresent(present -> valueCodec.encode(buffer, present));
                },
                buffer -> buffer.readBoolean()
                        ? Optional.of(valueCodec.decode(buffer))
                        : Optional.empty()
        );
    }

    private static void validateCollectionSize(int size) {
        if (size < 0 || size > MAX_COLLECTION_SIZE) {
            throw new IllegalArgumentException("Invalid network collection size: " + size);
        }
    }

    private static int readCollectionSize(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        validateCollectionSize(size);
        return size;
    }

    private static final class ItemStackCodecHolder {
        private static final StreamCodec<RegistryFriendlyByteBuf, ItemStack> CODEC = ItemStack.OPTIONAL_STREAM_CODEC;
    }
}

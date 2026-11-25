package dev.arakiel.gemini.network.internal;

import dev.arakiel.gemini.Gemini;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record NetworkBundle(List<Entry> entries) implements CustomPacketPayload {
    public static final int MAX_PACKET_SIZE = 1_048_576;
    public static final int MAX_ENTRY_SIZE = 524_288;
    public static final int MAX_ENTRIES = 256;
    public static final int MAX_PATH_LENGTH = 128;
    public static final CustomPacketPayload.Type<NetworkBundle> TYPE =
            new CustomPacketPayload.Type<>(Gemini.id("bundle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NetworkBundle> CODEC = StreamCodec.of(
            (buffer, bundle) -> {
                validate(bundle);
                buffer.writeVarInt(bundle.entries().size());
                for (Entry entry : bundle.entries()) {
                    buffer.writeResourceLocation(entry.channel());
                    buffer.writeUtf(entry.path(), MAX_PATH_LENGTH);
                    buffer.writeVarInt(entry.data().length);
                    buffer.writeBytes(entry.data());
                }
            },
            buffer -> {
                int count = buffer.readVarInt();
                if (count < 0 || count > MAX_ENTRIES) {
                    throw new IllegalArgumentException("Invalid bundle entry count: " + count);
                }
                List<Entry> entries = new ArrayList<>(count);
                int size = 4;
                for (int i = 0; i < count; i++) {
                    ResourceLocation channel = buffer.readResourceLocation();
                    String path = buffer.readUtf(MAX_PATH_LENGTH);
                    int length = buffer.readVarInt();
                    if (length < 0 || length > MAX_ENTRY_SIZE) {
                        throw new IllegalArgumentException("Invalid bundle entry size: " + length);
                    }
                    size += encodedSize(channel, path, length);
                    if (size > MAX_PACKET_SIZE) {
                        throw new IllegalArgumentException("Bundle exceeds packet size limit: " + size);
                    }
                    byte[] data = new byte[length];
                    buffer.readBytes(data);
                    entries.add(new Entry(channel, path, data));
                }
                return new NetworkBundle(entries);
            }
    );

    public NetworkBundle {
        entries = List.copyOf(entries);
    }

    public static List<NetworkBundle> partition(List<Entry> entries) {
        Objects.requireNonNull(entries, "entries");
        if (entries.isEmpty()) {
            return List.of();
        }
        List<NetworkBundle> bundles = new ArrayList<>();
        List<Entry> current = new ArrayList<>();
        int currentSize = 4;
        for (Entry entry : entries) {
            validateEntry(entry);
            int entrySize = encodedSize(entry);
            if (!current.isEmpty()
                    && (current.size() >= MAX_ENTRIES || currentSize + entrySize > MAX_PACKET_SIZE)) {
                bundles.add(new NetworkBundle(current));
                current = new ArrayList<>();
                currentSize = 4;
            }
            current.add(entry);
            currentSize += entrySize;
        }
        if (!current.isEmpty()) {
            bundles.add(new NetworkBundle(current));
        }
        return List.copyOf(bundles);
    }

    public static void validate(NetworkBundle bundle) {
        if (bundle.entries().size() > MAX_ENTRIES) {
            throw new IllegalArgumentException("Too many bundle entries: " + bundle.entries().size());
        }
        int size = 4;
        for (Entry entry : bundle.entries()) {
            validateEntry(entry);
            size += encodedSize(entry);
        }
        if (size > MAX_PACKET_SIZE) {
            throw new IllegalArgumentException("Bundle exceeds packet size limit: " + size);
        }
    }

    public static void validateEntry(Entry entry) {
        Objects.requireNonNull(entry.channel(), "entry.channel");
        String path = Objects.requireNonNull(entry.path(), "entry.path");
        if (path.isBlank() || path.length() > MAX_PATH_LENGTH) {
            throw new IllegalArgumentException("Invalid bundle entry path: " + path);
        }
        int length = Objects.requireNonNull(entry.data(), "entry.data").length;
        if (length > MAX_ENTRY_SIZE) {
            throw new IllegalArgumentException("Bundle entry exceeds size limit: " + length);
        }
    }

    private static int encodedSize(Entry entry) {
        return encodedSize(entry.channel(), entry.path(), entry.data().length);
    }

    private static int encodedSize(ResourceLocation channel, String path, int dataLength) {
        return 4 + utfSize(channel.toString()) + utfSize(path) + varIntSize(dataLength) + dataLength;
    }

    private static int utfSize(String value) {
        return 3 + value.length() * 3;
    }

    private static int varIntSize(int value) {
        int size = 1;
        while ((value & -128) != 0) {
            value >>>= 7;
            size++;
        }
        return size;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record Entry(ResourceLocation channel, String path, byte[] data) {
        public Entry {
            channel = Objects.requireNonNull(channel, "channel");
            path = Objects.requireNonNull(path, "path");
            data = Objects.requireNonNull(data, "data").clone();
        }
    }
}

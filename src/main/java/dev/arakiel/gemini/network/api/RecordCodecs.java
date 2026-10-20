package dev.arakiel.gemini.network.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RecordCodecs {
    private static final Map<Class<?>, StreamCodec<RegistryFriendlyByteBuf, ?>> RECORD_CACHE = new ConcurrentHashMap<>();

    private RecordCodecs() {
    }

    public static <R extends Record> StreamCodec<RegistryFriendlyByteBuf, R> create(Class<R> recordType) {
        return asType(createRecord(recordType));
    }

    @SuppressWarnings("unchecked")
    private static <R extends Record> StreamCodec<RegistryFriendlyByteBuf, R> createRecord(Class<R> recordType) {
        StreamCodec<RegistryFriendlyByteBuf, ?> cached = RECORD_CACHE.get(recordType);
        if (cached != null) {
            return (StreamCodec<RegistryFriendlyByteBuf, R>) cached;
        }
        LazyCodec<R> lazy = new LazyCodec<>();
        StreamCodec<RegistryFriendlyByteBuf, ?> concurrent = RECORD_CACHE.putIfAbsent(recordType, lazy);
        if (concurrent != null) {
            return (StreamCodec<RegistryFriendlyByteBuf, R>) concurrent;
        }

        try {
            RecordComponent[] components = recordType.getRecordComponents();
            Class<?>[] parameterTypes = new Class<?>[components.length];
            StreamCodec<RegistryFriendlyByteBuf, Object>[] componentCodecs = new StreamCodec[components.length];
            for (int i = 0; i < components.length; i++) {
                parameterTypes[i] = components[i].getType();
                componentCodecs[i] = create(components[i].getGenericType());
                components[i].getAccessor().setAccessible(true);
            }

            Constructor<R> constructor;
            try {
                constructor = recordType.getDeclaredConstructor(parameterTypes);
                constructor.setAccessible(true);
            } catch (NoSuchMethodException exception) {
                throw new IllegalArgumentException("Record has no canonical constructor: " + recordType.getName(), exception);
            }

            StreamCodec<RegistryFriendlyByteBuf, R> codec = StreamCodec.of(
                    (buffer, value) -> {
                        for (int i = 0; i < components.length; i++) {
                            try {
                                componentCodecs[i].encode(buffer, components[i].getAccessor().invoke(value));
                            } catch (IllegalAccessException | InvocationTargetException exception) {
                                throw new IllegalStateException("Cannot encode record component " + components[i].getName(), exception);
                            }
                        }
                    },
                    buffer -> {
                        Object[] values = new Object[components.length];
                        for (int i = 0; i < components.length; i++) {
                            values[i] = componentCodecs[i].decode(buffer);
                        }
                        try {
                            return constructor.newInstance(values);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
                            throw new IllegalStateException("Cannot decode record " + recordType.getName(), exception);
                        }
                    }
            );
            lazy.delegate = codec;
            return lazy;
        } catch (RuntimeException | Error exception) {
            RECORD_CACHE.remove(recordType, lazy);
            throw exception;
        }
    }

    private static StreamCodec<RegistryFriendlyByteBuf, Object> create(Type type) {
        if (type instanceof Class<?> rawType) {
            if (rawType == boolean.class || rawType == Boolean.class) {
                return asObject(NetworkCodecs.BOOL);
            }
            if (rawType == byte.class || rawType == Byte.class) {
                return asObject(NetworkCodecs.BYTE);
            }
            if (rawType == short.class || rawType == Short.class) {
                return asObject(NetworkCodecs.SHORT);
            }
            if (rawType == char.class || rawType == Character.class) {
                return asObject(NetworkCodecs.CHAR);
            }
            if (rawType == int.class || rawType == Integer.class) {
                return asObject(NetworkCodecs.VAR_INT);
            }
            if (rawType == long.class || rawType == Long.class) {
                return asObject(NetworkCodecs.VAR_LONG);
            }
            if (rawType == float.class || rawType == Float.class) {
                return asObject(NetworkCodecs.FLOAT);
            }
            if (rawType == double.class || rawType == Double.class) {
                return asObject(NetworkCodecs.DOUBLE);
            }
            if (rawType == byte[].class) {
                return asObject(NetworkCodecs.byteArray());
            }
            if (rawType == String.class) {
                return asObject(NetworkCodecs.STRING);
            }
            if (rawType == ResourceLocation.class) {
                return asObject(NetworkCodecs.RESOURCE_LOCATION);
            }
            if (rawType == ItemStack.class) {
                return asObject(NetworkCodecs.itemStack());
            }
            if (rawType == Vec3.class) {
                return asObject(NetworkCodecs.VEC3);
            }
            if (rawType == BlockPos.class) {
                return asObject(NetworkCodecs.BLOCK_POS);
            }
            if (rawType == UUID.class) {
                return asObject(NetworkCodecs.UUID);
            }
            if (rawType.isEnum()) {
                return enumObject(rawType);
            }
            if (rawType.isRecord()) {
                return createRecordObject(rawType);
            }
            throw new IllegalArgumentException("Unsupported record component type: " + rawType.getName());
        }

        if (type instanceof ParameterizedType parameterized) {
            Type raw = parameterized.getRawType();
            Type[] arguments = parameterized.getActualTypeArguments();
            if (raw == List.class) {
                return asObject(NetworkCodecs.list(create(arguments[0])));
            }
            if (raw == Set.class) {
                return asObject(NetworkCodecs.set(create(arguments[0])));
            }
            if (raw == Optional.class) {
                return asObject(NetworkCodecs.optional(create(arguments[0])));
            }
            if (raw == Map.class) {
                return asObject(NetworkCodecs.map(create(arguments[0]), create(arguments[1])));
            }
        }

        throw new IllegalArgumentException("Unsupported record component type: " + type.getTypeName());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static StreamCodec<RegistryFriendlyByteBuf, Object> enumObject(Class<?> type) {
        return asObject(NetworkCodecs.enumCodec((Class) type));
    }

    @SuppressWarnings("unchecked")
    private static <T> StreamCodec<RegistryFriendlyByteBuf, Object> asObject(StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        return (StreamCodec<RegistryFriendlyByteBuf, Object>) (StreamCodec<?, ?>) codec;
    }

    @SuppressWarnings("unchecked")
    private static StreamCodec<RegistryFriendlyByteBuf, Object> createRecordObject(Class<?> type) {
        return asObject(createRecord((Class<? extends Record>) type));
    }

    @SuppressWarnings("unchecked")
    private static <T> StreamCodec<RegistryFriendlyByteBuf, T> asType(Object codec) {
        return (StreamCodec<RegistryFriendlyByteBuf, T>) codec;
    }

    private static final class LazyCodec<T> implements StreamCodec<RegistryFriendlyByteBuf, T> {
        private volatile StreamCodec<RegistryFriendlyByteBuf, T> delegate;

        @Override
        public T decode(RegistryFriendlyByteBuf buffer) {
            return delegate().decode(buffer);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, T value) {
            delegate().encode(buffer, value);
        }

        private StreamCodec<RegistryFriendlyByteBuf, T> delegate() {
            StreamCodec<RegistryFriendlyByteBuf, T> current = delegate;
            if (current == null) {
                throw new IllegalStateException("Recursive record codec was not initialized");
            }
            return current;
        }
    }
}

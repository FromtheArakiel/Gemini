package dev.arakiel.gemini.component.api;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class TokenKey<T extends ConditionalToken> {
    private static final Map<ResourceLocation, TokenKey<?>> KEYS = new ConcurrentHashMap<>();

    private final ResourceLocation id;
    private final Class<T> type;
    private final Supplier<T> factory;

    public TokenKey(ResourceLocation id, Class<T> type, Supplier<T> factory) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.factory = Objects.requireNonNull(factory, "factory");
        TokenKey<?> existing = KEYS.putIfAbsent(id, this);
        if (existing != null && existing != this) {
            throw new IllegalStateException("Conditional token already registered: " + id);
        }
    }

    public ResourceLocation id() {
        return id;
    }

    public Class<T> type() {
        return type;
    }

    public T create() {
        return factory.get();
    }

    public static TokenKey<?> get(ResourceLocation id) {
        return KEYS.get(id);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}

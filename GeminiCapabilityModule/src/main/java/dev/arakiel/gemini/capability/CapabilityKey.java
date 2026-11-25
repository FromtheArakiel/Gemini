package dev.arakiel.gemini.capability;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record CapabilityKey<T>(ResourceLocation id) {
    public CapabilityKey {
        Objects.requireNonNull(id, "id");
    }

    public static <T> CapabilityKey<T> create(ResourceLocation id) {
        return new CapabilityKey<>(id);
    }
}

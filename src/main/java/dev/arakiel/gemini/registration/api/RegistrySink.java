package dev.arakiel.gemini.registration.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface RegistrySink<T> {
    T register(ResourceLocation id, T value);

    static <T> RegistrySink<T> from(Registry<T> registry) {
        return (id, value) -> Registry.register(registry, id, value);
    }
}

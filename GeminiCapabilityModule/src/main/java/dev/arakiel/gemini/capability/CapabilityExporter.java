package dev.arakiel.gemini.capability;

import net.minecraft.resources.ResourceLocation;

public interface CapabilityExporter<T> {
    ResourceLocation id();

    Object export(T capability);
}

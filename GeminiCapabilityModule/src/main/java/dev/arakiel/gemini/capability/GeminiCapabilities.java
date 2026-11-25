package dev.arakiel.gemini.capability;

import dev.arakiel.gemini.Gemini;

public final class GeminiCapabilities {
    public static final CapabilityKey<ItemStorageView> ITEM_STORAGE =
            CapabilityKey.create(Gemini.id("item_storage"));
    public static final CapabilityKey<FluidStorageView> FLUID_STORAGE =
            CapabilityKey.create(Gemini.id("fluid_storage"));
    public static final CapabilityKey<EnergyStorageView> ENERGY =
            CapabilityKey.create(Gemini.id("energy"));

    private static final CapabilityRegistry REGISTRY = new CapabilityRegistry();

    private GeminiCapabilities() {
    }

    public static CapabilityRegistry registry() {
        return REGISTRY;
    }
}

package dev.arakiel.gemini.component.internal.itemstack;

import dev.arakiel.gemini.component.ComponentContainer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

final class ItemStackComponentRuntime {
    private final ComponentContainer container;
    private CompoundTag unknownComponents;
    private Provider provider;

    ItemStackComponentRuntime(
            ComponentContainer container,
            CompoundTag unknownComponents,
            Provider provider
    ) {
        this.container = container;
        this.unknownComponents = unknownComponents.copy();
        this.provider = provider;
    }

    ComponentContainer container() {
        return this.container;
    }

    CompoundTag unknownComponents() {
        return this.unknownComponents.copy();
    }

    void replaceUnknownComponents(CompoundTag unknownComponents) {
        this.unknownComponents = unknownComponents.copy();
    }

    Provider provider() {
        return this.provider;
    }

    void setProvider(Provider provider) {
        this.provider = provider == null ? net.minecraft.core.RegistryAccess.EMPTY : provider;
    }
}

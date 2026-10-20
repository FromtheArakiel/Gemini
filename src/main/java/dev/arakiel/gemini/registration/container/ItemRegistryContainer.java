package dev.arakiel.gemini.registration.container;

import dev.arakiel.gemini.registration.api.AutoRegistryContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

public interface ItemRegistryContainer extends AutoRegistryContainer<Item> {
    @Override
    default Registry<Item> getRegistry() {
        return BuiltInRegistries.ITEM;
    }

    @Override
    default Class<Item> getTargetFieldType() {
        return Item.class;
    }
}

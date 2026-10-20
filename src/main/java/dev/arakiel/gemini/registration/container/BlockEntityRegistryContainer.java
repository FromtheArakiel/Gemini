package dev.arakiel.gemini.registration.container;

import dev.arakiel.gemini.registration.api.AutoRegistryContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface BlockEntityRegistryContainer extends AutoRegistryContainer<BlockEntityType<?>> {
    @Override
    default Registry<BlockEntityType<?>> getRegistry() {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Class<BlockEntityType<?>> getTargetFieldType() {
        return (Class<BlockEntityType<?>>) (Class<?>) BlockEntityType.class;
    }
}

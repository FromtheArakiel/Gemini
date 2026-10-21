package dev.arakiel.gemini.registration.container;

import dev.arakiel.gemini.registration.AutoRegistryContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

public interface EntityRegistryContainer extends AutoRegistryContainer<EntityType<?>> {
    @Override
    default Registry<EntityType<?>> getRegistry() {
        return BuiltInRegistries.ENTITY_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Class<EntityType<?>> getTargetFieldType() {
        return (Class<EntityType<?>>) (Class<?>) EntityType.class;
    }
}

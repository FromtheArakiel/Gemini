package dev.arakiel.gemini.registration.container;

import dev.arakiel.gemini.registration.AutoRegistryContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public interface MobEffectRegistryContainer extends AutoRegistryContainer<MobEffect> {
    @Override
    default Registry<MobEffect> getRegistry() {
        return BuiltInRegistries.MOB_EFFECT;
    }

    @Override
    default Class<MobEffect> getTargetFieldType() {
        return MobEffect.class;
    }
}

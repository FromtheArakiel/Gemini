package dev.arakiel.gemini.registration.container;

import dev.arakiel.gemini.registration.api.AutoRegistryContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public interface SoundEventRegistryContainer extends AutoRegistryContainer<SoundEvent> {
    @Override
    default Registry<SoundEvent> getRegistry() {
        return BuiltInRegistries.SOUND_EVENT;
    }

    @Override
    default Class<SoundEvent> getTargetFieldType() {
        return SoundEvent.class;
    }
}

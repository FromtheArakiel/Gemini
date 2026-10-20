package dev.arakiel.gemini.event.effect;

import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class MobEffectRemovedEvent extends LivingEvent {
    private final MobEffectInstance effect;

    public MobEffectRemovedEvent(LivingEntity entity, MobEffectInstance effect) {
        super(entity);
        this.effect = Objects.requireNonNull(effect, "effect");
    }

    public MobEffectInstance getEffect() {
        return effect;
    }
}

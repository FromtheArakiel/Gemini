package dev.arakiel.gemini.event.effect;

import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class MobEffectAddedEvent extends LivingEvent {
    private final MobEffectInstance effect;
    private final MobEffectInstance previousEffect;
    private final Entity source;

    public MobEffectAddedEvent(
            LivingEntity entity,
            MobEffectInstance effect,
            MobEffectInstance previousEffect,
            Entity source
    ) {
        super(entity);
        this.effect = Objects.requireNonNull(effect, "effect");
        this.previousEffect = previousEffect;
        this.source = source;
    }

    public MobEffectInstance getEffect() {
        return effect;
    }

    public MobEffectInstance getPreviousEffect() {
        return previousEffect;
    }

    public Entity getSource() {
        return source;
    }
}

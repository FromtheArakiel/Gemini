package dev.arakiel.gemini.event.effect;

import dev.arakiel.gemini.event.api.HasResult;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

@HasResult
public class MobEffectApplicableEvent extends LivingEvent {
    private final MobEffectInstance effect;

    public MobEffectApplicableEvent(LivingEntity entity, MobEffectInstance effect) {
        super(entity);
        this.effect = Objects.requireNonNull(effect, "effect");
    }

    public MobEffectInstance getEffect() {
        return effect;
    }
}

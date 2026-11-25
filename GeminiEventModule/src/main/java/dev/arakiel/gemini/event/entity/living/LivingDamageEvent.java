package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

@Cancelable
public class LivingDamageEvent extends LivingEvent {
    private final DamageSource source;
    private float amount;

    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        super(entity);
        this.source = Objects.requireNonNull(source, "source");
        this.amount = amount;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}

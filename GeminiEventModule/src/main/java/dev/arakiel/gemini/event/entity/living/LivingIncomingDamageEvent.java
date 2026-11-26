package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

@Cancelable
public class LivingIncomingDamageEvent extends LivingEvent {
    private final DamageSource source;
    private final float originalAmount;
    private float amount;

    public LivingIncomingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        this(entity, source, amount, amount);
    }

    public LivingIncomingDamageEvent(
            LivingEntity entity,
            DamageSource source,
            float originalAmount,
            float amount
    ) {
        super(entity);
        this.source = Objects.requireNonNull(source, "source");
        this.originalAmount = Math.max(0.0F, originalAmount);
        this.amount = Math.max(0.0F, amount);
    }

    public DamageSource getSource() {
        return this.source;
    }

    public float getAmount() {
        return this.amount;
    }

    public float getOriginalAmount() {
        return this.originalAmount;
    }

    public void setAmount(float amount) {
        this.amount = Math.max(0.0F, amount);
    }
}

package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class LivingDamageEvent extends LivingEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final DamageSource source;
    private final Phase phase;
    private final float originalAmount;
    private float amount;
    private float blockedDamage;
    private float shieldDamage;
    private int postAttackInvulnerabilityTicks;

    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        this(entity, source, amount, Phase.PRE);
    }

    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount, Phase phase) {
        this(entity, source, amount, amount, phase);
    }

    public LivingDamageEvent(
            LivingEntity entity,
            DamageSource source,
            float originalAmount,
            float amount,
            Phase phase
    ) {
        super(entity);
        this.source = Objects.requireNonNull(source, "source");
        this.phase = Objects.requireNonNull(phase, "phase");
        this.originalAmount = Math.max(0.0F, originalAmount);
        this.amount = Math.max(0.0F, amount);
    }

    public DamageSource getSource() {
        return source;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public float getAmount() {
        return amount;
    }

    public float getNewDamage() {
        return this.amount;
    }

    public float getOriginalAmount() {
        return this.originalAmount;
    }

    public void setAmount(float amount) {
        if (this.phase == Phase.POST) {
            throw new UnsupportedOperationException("Post damage result is read-only");
        }
        this.amount = Math.max(0.0F, amount);
    }

    public float getBlockedDamage() {
        return this.blockedDamage;
    }

    public void setBlockedDamage(float blockedDamage) {
        this.blockedDamage = Math.max(0.0F, blockedDamage);
    }

    public float getShieldDamage() {
        return this.shieldDamage;
    }

    public void setShieldDamage(float shieldDamage) {
        this.shieldDamage = Math.max(0.0F, shieldDamage);
    }

    public int getPostAttackInvulnerabilityTicks() {
        return this.postAttackInvulnerabilityTicks;
    }

    public void setPostAttackInvulnerabilityTicks(int ticks) {
        this.postAttackInvulnerabilityTicks = Math.max(0, ticks);
    }
}

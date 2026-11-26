package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

@Cancelable
public class LivingShieldBlockEvent extends LivingEvent {
    private final DamageSource source;
    private final boolean originalBlocked;
    private boolean blocked;
    private float blockedDamage;
    private float shieldDamage;

    public LivingShieldBlockEvent(LivingEntity entity, DamageSource source, float blockedAmount) {
        this(entity, source, blockedAmount, blockedAmount > 0.0F);
    }

    public LivingShieldBlockEvent(
            LivingEntity entity,
            DamageSource source,
            float blockedAmount,
            boolean originalBlocked
    ) {
        super(entity);
        this.source = Objects.requireNonNull(source, "source");
        this.originalBlocked = originalBlocked;
        this.blocked = originalBlocked;
        this.blockedDamage = Math.max(0.0F, blockedAmount);
        this.shieldDamage = Math.max(0.0F, blockedAmount);
    }

    public DamageSource getSource() {
        return this.source;
    }

    public float getBlockedAmount() {
        return this.blockedDamage;
    }

    public void setBlockedAmount(float blockedAmount) {
        this.blockedDamage = Math.max(0.0F, blockedAmount);
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

    public boolean getOriginalBlock() {
        return this.originalBlocked;
    }

    public boolean getBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}

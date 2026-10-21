package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.LivingEntity;

@Cancelable
public class LivingKnockbackEvent extends LivingEvent {
    private final double strength;
    private final double xRatio;
    private final double zRatio;

    public LivingKnockbackEvent(LivingEntity entity, double strength, double xRatio, double zRatio) {
        super(entity);
        this.strength = strength;
        this.xRatio = xRatio;
        this.zRatio = zRatio;
    }

    public double getStrength() {
        return strength;
    }

    public double getXRatio() {
        return xRatio;
    }

    public double getZRatio() {
        return zRatio;
    }
}

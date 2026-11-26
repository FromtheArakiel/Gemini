package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.LivingEntity;

@Cancelable
public class LivingDrownEvent extends LivingEvent {
    private boolean drowning;
    private float damageAmount;
    private int bubbleCount;
    private int airSupply;

    public LivingDrownEvent(LivingEntity entity, int airSupply) {
        this(entity, true, 2.0F, 8, airSupply);
    }

    public LivingDrownEvent(
            LivingEntity entity,
            boolean drowning,
            float damageAmount,
            int bubbleCount,
            int airSupply
    ) {
        super(entity);
        this.drowning = drowning;
        this.damageAmount = Math.max(0.0F, damageAmount);
        this.bubbleCount = Math.max(0, bubbleCount);
        this.airSupply = Math.max(0, airSupply);
    }

    public boolean isDrowning() {
        return this.drowning;
    }

    public void setDrowning(boolean drowning) {
        this.drowning = drowning;
    }

    public float getDamageAmount() {
        return this.damageAmount;
    }

    public void setDamageAmount(float damageAmount) {
        this.damageAmount = Math.max(0.0F, damageAmount);
    }

    public int getBubbleCount() {
        return this.bubbleCount;
    }

    public void setBubbleCount(int bubbleCount) {
        this.bubbleCount = Math.max(0, bubbleCount);
    }

    public int getAirSupply() {
        return this.airSupply;
    }

    public void setAirSupply(int airSupply) {
        this.airSupply = Math.max(0, airSupply);
    }
}

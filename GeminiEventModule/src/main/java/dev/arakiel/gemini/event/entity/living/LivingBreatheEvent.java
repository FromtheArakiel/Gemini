package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.LivingEntity;

public class LivingBreatheEvent extends LivingEvent {
    private boolean canBreathe;
    private int consumeAirAmount;
    private int refillAirAmount;

    public LivingBreatheEvent(
            LivingEntity entity,
            boolean canBreathe,
            int consumeAirAmount,
            int refillAirAmount
    ) {
        super(entity);
        this.canBreathe = canBreathe;
        this.consumeAirAmount = Math.max(0, consumeAirAmount);
        this.refillAirAmount = Math.max(0, refillAirAmount);
    }

    public LivingBreatheEvent(LivingEntity entity, boolean canBreathe) {
        this(entity, canBreathe, 1, 4);
    }

    public boolean canBreathe() {
        return this.canBreathe;
    }

    public void setCanBreathe(boolean canBreathe) {
        this.canBreathe = canBreathe;
    }

    public int getConsumeAirAmount() {
        return this.consumeAirAmount;
    }

    public void setConsumeAirAmount(int consumeAirAmount) {
        this.consumeAirAmount = Math.max(0, consumeAirAmount);
    }

    public int getRefillAirAmount() {
        return this.refillAirAmount;
    }

    public void setRefillAirAmount(int refillAirAmount) {
        this.refillAirAmount = Math.max(0, refillAirAmount);
    }
}

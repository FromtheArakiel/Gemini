package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.LivingEntity;

@Cancelable
public class LivingHealEvent extends LivingEvent {
    private float amount;

    public LivingHealEvent(LivingEntity entity, float amount) {
        super(entity);
        this.amount = amount;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}

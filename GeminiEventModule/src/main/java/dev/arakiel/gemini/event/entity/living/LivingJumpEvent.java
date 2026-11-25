package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.LivingEntity;

public class LivingJumpEvent extends LivingEvent {
    public LivingJumpEvent(LivingEntity entity) {
        super(entity);
    }
}

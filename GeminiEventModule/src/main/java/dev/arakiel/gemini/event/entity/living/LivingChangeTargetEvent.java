package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@Cancelable
public class LivingChangeTargetEvent extends LivingEvent {
    private final @Nullable LivingEntity originalTarget;
    private @Nullable LivingEntity newTarget;

    public LivingChangeTargetEvent(LivingEntity entity, @Nullable LivingEntity newTarget) {
        super(entity);
        this.originalTarget = newTarget;
        this.newTarget = newTarget;
    }

    public @Nullable LivingEntity getOriginalTarget() {
        return this.originalTarget;
    }

    public @Nullable LivingEntity getNewTarget() {
        return this.newTarget;
    }

    public void setNewTarget(@Nullable LivingEntity newTarget) {
        this.newTarget = newTarget;
    }
}

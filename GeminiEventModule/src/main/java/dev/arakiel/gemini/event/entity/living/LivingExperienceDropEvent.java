package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Cancelable
public class LivingExperienceDropEvent extends LivingEvent {
    private final @Nullable Player attackingPlayer;
    private final int originalExperience;
    private int droppedExperience;

    public LivingExperienceDropEvent(
            LivingEntity entity,
            @Nullable Player attackingPlayer,
            int originalExperience
    ) {
        super(entity);
        this.attackingPlayer = attackingPlayer;
        this.originalExperience = Math.max(0, originalExperience);
        this.droppedExperience = this.originalExperience;
    }

    public @Nullable Player getAttackingPlayer() {
        return this.attackingPlayer;
    }

    public int getOriginalExperience() {
        return this.originalExperience;
    }

    public int getDroppedExperience() {
        return this.droppedExperience;
    }

    public void setDroppedExperience(int droppedExperience) {
        this.droppedExperience = Math.max(0, droppedExperience);
    }
}

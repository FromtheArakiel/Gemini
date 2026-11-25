package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Cancelable
public class LivingConversionEvent extends LivingEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final EntityType<? extends Mob> targetType;
    private final Phase phase;
    private final @Nullable Mob replacement;

    public LivingConversionEvent(
            LivingEntity entity,
            EntityType<? extends Mob> targetType,
            Phase phase,
            @Nullable Mob replacement
    ) {
        super(entity);
        this.targetType = Objects.requireNonNull(targetType, "targetType");
        this.phase = Objects.requireNonNull(phase, "phase");
        this.replacement = replacement;
    }

    public EntityType<? extends Mob> getTargetType() {
        return this.targetType;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public @Nullable Mob getReplacement() {
        return this.replacement;
    }
}

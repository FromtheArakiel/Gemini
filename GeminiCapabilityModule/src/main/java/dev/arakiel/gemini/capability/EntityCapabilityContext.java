package dev.arakiel.gemini.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record EntityCapabilityContext(Entity entity, @Nullable Direction direction) implements CapabilityContext {
    public EntityCapabilityContext {
        Objects.requireNonNull(entity, "entity");
    }
}

package dev.arakiel.gemini.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record LevelCapabilityContext(Level level) implements CapabilityContext {
    public LevelCapabilityContext {
        Objects.requireNonNull(level, "level");
    }

    @Override
    public @Nullable Direction direction() {
        return null;
    }
}

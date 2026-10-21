package dev.arakiel.gemini.event.level;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class LevelEvent extends GeminiEvent {
    private final Level level;

    public LevelEvent(Level level) {
        this.level = Objects.requireNonNull(level, "level");
    }

    public Level getLevel() {
        return level;
    }
}

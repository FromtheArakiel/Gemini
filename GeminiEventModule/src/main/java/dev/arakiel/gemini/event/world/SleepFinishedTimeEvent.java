package dev.arakiel.gemini.event.world;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

public class SleepFinishedTimeEvent extends GeminiEvent {
    private final ServerLevel level;
    private long newTime;

    public SleepFinishedTimeEvent(ServerLevel level, long newTime) {
        this.level = Objects.requireNonNull(level, "level");
        this.newTime = newTime;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public long getNewTime() {
        return this.newTime;
    }

    public void setNewTime(long newTime) {
        this.newTime = newTime;
    }
}

package dev.arakiel.gemini.event.chunk;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.Objects;

public class ChunkWatchEvent extends GeminiEvent {
    public enum Phase {
        WATCH,
        UNWATCH
    }

    private final ServerLevel level;
    private final ServerPlayer player;
    private final ChunkPos pos;
    private final Phase phase;

    public ChunkWatchEvent(ServerLevel level, ServerPlayer player, ChunkPos pos, Phase phase) {
        this.level = Objects.requireNonNull(level, "level");
        this.player = Objects.requireNonNull(player, "player");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public Phase getPhase() {
        return this.phase;
    }
}

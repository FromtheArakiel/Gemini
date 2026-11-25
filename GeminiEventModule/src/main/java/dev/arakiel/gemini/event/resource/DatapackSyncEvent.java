package dev.arakiel.gemini.event.resource;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class DatapackSyncEvent extends GeminiEvent {
    private final ServerPlayer player;
    private final boolean joined;

    public DatapackSyncEvent(ServerPlayer player, boolean joined) {
        this.player = Objects.requireNonNull(player, "player");
        this.joined = joined;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public boolean isJoined() {
        return this.joined;
    }
}

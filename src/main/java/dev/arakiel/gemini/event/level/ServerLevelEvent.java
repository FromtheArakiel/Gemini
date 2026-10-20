package dev.arakiel.gemini.event.level;

import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

public class ServerLevelEvent extends LevelEvent {
    private final ServerLevel serverLevel;

    public ServerLevelEvent(ServerLevel serverLevel) {
        super(Objects.requireNonNull(serverLevel, "serverLevel"));
        this.serverLevel = serverLevel;
    }

    public ServerLevel getServerLevel() {
        return serverLevel;
    }
}

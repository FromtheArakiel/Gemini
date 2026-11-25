package dev.arakiel.gemini.event.server;

import net.minecraft.server.MinecraftServer;

import java.util.Objects;

public class ServerLifecycleEvent extends ServerEvent {
    private final Stage stage;

    public ServerLifecycleEvent(MinecraftServer server, Stage stage) {
        super(server);
        this.stage = Objects.requireNonNull(stage, "stage");
    }

    public Stage getStage() {
        return stage;
    }

    public enum Stage {
        STARTING,
        STARTED,
        STOPPING,
        STOPPED
    }
}

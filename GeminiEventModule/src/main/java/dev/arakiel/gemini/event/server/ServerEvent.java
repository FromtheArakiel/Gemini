package dev.arakiel.gemini.event.server;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;

public class ServerEvent extends GeminiEvent {
    private final MinecraftServer server;

    public ServerEvent(MinecraftServer server) {
        this.server = Objects.requireNonNull(server, "server");
    }

    public MinecraftServer getServer() {
        return server;
    }
}

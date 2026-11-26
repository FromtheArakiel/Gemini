package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.network.Connection;

import java.util.Objects;

@Cancelable
public class PlayerNegotiationEvent extends GeminiEvent {
    private final Connection connection;

    public PlayerNegotiationEvent(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "connection");
    }

    public Connection getConnection() {
        return this.connection;
    }
}

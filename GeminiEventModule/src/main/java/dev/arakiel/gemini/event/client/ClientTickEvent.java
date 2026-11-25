package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.Minecraft;

import java.util.Objects;

public class ClientTickEvent extends GeminiEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final Minecraft client;
    private final Phase phase;

    public ClientTickEvent(Minecraft client, Phase phase) {
        this.client = Objects.requireNonNull(client, "client");
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public Minecraft getClient() {
        return this.client;
    }

    public Phase getPhase() {
        return this.phase;
    }
}

package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ClientPlayerNetworkEvent extends GeminiEvent {
    public enum Phase {
        LOGGING_IN,
        LOGGING_OUT,
        CLONE
    }

    private final Phase phase;
    private final @Nullable LocalPlayer player;
    private final @Nullable LocalPlayer replacement;

    public ClientPlayerNetworkEvent(Phase phase, @Nullable LocalPlayer player, @Nullable LocalPlayer replacement) {
        this.phase = Objects.requireNonNull(phase, "phase");
        this.player = player;
        this.replacement = replacement;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public @Nullable LocalPlayer getPlayer() {
        return this.player;
    }

    public @Nullable LocalPlayer getReplacement() {
        return this.replacement;
    }
}

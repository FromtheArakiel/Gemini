package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class PlayerRespawnEvent extends GeminiEvent {
    private final ServerPlayer original;
    private final ServerPlayer replacement;
    private final boolean wasDeath;

    public PlayerRespawnEvent(ServerPlayer original, ServerPlayer replacement, boolean wasDeath) {
        this.original = Objects.requireNonNull(original, "original");
        this.replacement = Objects.requireNonNull(replacement, "replacement");
        this.wasDeath = wasDeath;
    }

    public ServerPlayer getOriginal() {
        return this.original;
    }

    public ServerPlayer getReplacement() {
        return this.replacement;
    }

    public boolean wasDeath() {
        return this.wasDeath;
    }
}

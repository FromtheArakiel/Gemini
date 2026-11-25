package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class PlayerChangedDimensionEvent extends GeminiEvent {
    private final ServerPlayer player;
    private final ResourceKey<Level> from;
    private final ResourceKey<Level> to;

    public PlayerChangedDimensionEvent(ServerPlayer player, ResourceKey<Level> from, ResourceKey<Level> to) {
        this.player = Objects.requireNonNull(player, "player");
        this.from = Objects.requireNonNull(from, "from");
        this.to = Objects.requireNonNull(to, "to");
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public ResourceKey<Level> getFrom() {
        return this.from;
    }

    public ResourceKey<Level> getTo() {
        return this.to;
    }
}

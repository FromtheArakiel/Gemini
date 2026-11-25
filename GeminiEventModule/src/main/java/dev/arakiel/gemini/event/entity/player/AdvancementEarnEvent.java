package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class AdvancementEarnEvent extends GeminiEvent {
    private final ServerPlayer player;
    private final AdvancementHolder advancement;

    public AdvancementEarnEvent(ServerPlayer player, AdvancementHolder advancement) {
        this.player = Objects.requireNonNull(player, "player");
        this.advancement = Objects.requireNonNull(advancement, "advancement");
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public AdvancementHolder getAdvancement() {
        return this.advancement;
    }
}

package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class PlayerItemCraftedEvent extends GeminiEvent {
    private final Player player;
    private final ItemStack result;
    private final ItemStack crafted;

    public PlayerItemCraftedEvent(Player player, ItemStack result, ItemStack crafted) {
        this.player = Objects.requireNonNull(player, "player");
        this.result = Objects.requireNonNull(result, "result");
        this.crafted = Objects.requireNonNull(crafted, "crafted");
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getCraftedResult() {
        return this.result;
    }

    public ItemStack getCrafted() {
        return this.crafted;
    }
}

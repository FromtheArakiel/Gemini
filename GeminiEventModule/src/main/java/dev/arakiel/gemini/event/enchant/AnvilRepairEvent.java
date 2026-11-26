package dev.arakiel.gemini.event.enchant;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class AnvilRepairEvent extends GeminiEvent {
    private final Player player;
    private final ItemStack left;
    private final ItemStack right;
    private final ItemStack output;
    private float breakChance;

    public AnvilRepairEvent(Player player, ItemStack left, ItemStack right, ItemStack output, float breakChance) {
        this.player = Objects.requireNonNull(player, "player");
        this.left = Objects.requireNonNull(left, "left");
        this.right = Objects.requireNonNull(right, "right");
        this.output = Objects.requireNonNull(output, "output");
        this.breakChance = breakChance;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getLeft() {
        return this.left;
    }

    public ItemStack getRight() {
        return this.right;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public float getBreakChance() {
        return this.breakChance;
    }

    public void setBreakChance(float breakChance) {
        this.breakChance = Math.max(0.0F, Math.min(1.0F, breakChance));
    }
}

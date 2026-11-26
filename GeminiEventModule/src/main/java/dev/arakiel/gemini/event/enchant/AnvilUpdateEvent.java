package dev.arakiel.gemini.event.enchant;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Cancelable
public class AnvilUpdateEvent extends GeminiEvent {
    private final ItemStack left;
    private final ItemStack right;
    private final @Nullable String name;
    private ItemStack output;
    private int cost;

    public AnvilUpdateEvent(ItemStack left, ItemStack right, @Nullable String name, ItemStack output, int cost) {
        this.left = Objects.requireNonNull(left, "left");
        this.right = Objects.requireNonNull(right, "right");
        this.name = name;
        this.output = Objects.requireNonNull(output, "output");
        this.cost = Math.max(0, cost);
    }

    public ItemStack getLeft() {
        return this.left;
    }

    public ItemStack getRight() {
        return this.right;
    }

    public @Nullable String getName() {
        return this.name;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public void setOutput(ItemStack output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = Math.max(0, cost);
    }
}

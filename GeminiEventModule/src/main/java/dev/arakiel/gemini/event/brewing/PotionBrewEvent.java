package dev.arakiel.gemini.event.brewing;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class PotionBrewEvent extends GeminiEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final Phase phase;
    private final NonNullList<ItemStack> input;
    private final NonNullList<ItemStack> output;

    public PotionBrewEvent(NonNullList<ItemStack> input, NonNullList<ItemStack> output) {
        this(Phase.PRE, input, output);
    }

    public PotionBrewEvent(Phase phase, NonNullList<ItemStack> input, NonNullList<ItemStack> output) {
        this.phase = Objects.requireNonNull(phase, "phase");
        this.input = Objects.requireNonNull(input, "input");
        this.output = Objects.requireNonNull(output, "output");
    }

    public Phase getPhase() {
        return this.phase;
    }

    public NonNullList<ItemStack> getInput() {
        return this.input;
    }

    public NonNullList<ItemStack> getOutput() {
        return this.output;
    }

    public ItemStack getItem(int index) {
        return this.output.get(index);
    }

    public void setItem(int index, ItemStack stack) {
        this.output.set(index, Objects.requireNonNull(stack, "stack"));
    }

    public int getLength() {
        return this.output.size();
    }
}

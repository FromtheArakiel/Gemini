package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

@Cancelable
public class RenderTooltipEvent extends GeminiEvent {
    public enum Phase {
        GATHER_COMPONENTS,
        PRE,
        POST,
        COLOR
    }

    private final ItemStack stack;
    private final List<Component> lines;
    private final List<TooltipComponent> components;
    private final Phase phase;
    private int color;

    public RenderTooltipEvent(
            ItemStack stack,
            List<Component> lines,
            List<TooltipComponent> components,
            Phase phase,
            int color
    ) {
        this.stack = Objects.requireNonNull(stack, "stack");
        this.lines = Objects.requireNonNull(lines, "lines");
        this.components = Objects.requireNonNull(components, "components");
        this.phase = Objects.requireNonNull(phase, "phase");
        this.color = color;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public List<Component> getLines() {
        return this.lines;
    }

    public List<TooltipComponent> getComponents() {
        return this.components;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

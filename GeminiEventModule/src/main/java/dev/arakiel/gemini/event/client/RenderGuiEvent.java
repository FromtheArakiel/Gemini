package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Objects;

@Cancelable
public class RenderGuiEvent extends GeminiEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final GuiGraphics graphics;
    private final DeltaTracker deltaTracker;
    private final Phase phase;

    public RenderGuiEvent(GuiGraphics graphics, DeltaTracker deltaTracker, Phase phase) {
        this.graphics = Objects.requireNonNull(graphics, "graphics");
        this.deltaTracker = Objects.requireNonNull(deltaTracker, "deltaTracker");
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public GuiGraphics getGraphics() {
        return this.graphics;
    }

    public DeltaTracker getDeltaTracker() {
        return this.deltaTracker;
    }

    public Phase getPhase() {
        return this.phase;
    }
}

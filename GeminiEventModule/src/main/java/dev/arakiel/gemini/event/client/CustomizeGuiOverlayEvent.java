package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;
import java.util.Objects;

@Cancelable
public class CustomizeGuiOverlayEvent extends GeminiEvent {
    public enum Phase {
        DEBUG_TEXT,
        CHAT
    }

    private final GuiGraphics graphics;
    private final List<String> lines;
    private final Phase phase;

    public CustomizeGuiOverlayEvent(GuiGraphics graphics, List<String> lines, Phase phase) {
        this.graphics = Objects.requireNonNull(graphics, "graphics");
        this.lines = Objects.requireNonNull(lines, "lines");
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public GuiGraphics getGraphics() {
        return this.graphics;
    }

    public List<String> getLines() {
        return this.lines;
    }

    public Phase getPhase() {
        return this.phase;
    }
}

package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.Objects;

@Cancelable
public class ContainerScreenEvent extends GeminiEvent {
    public enum Phase {
        BACKGROUND,
        FOREGROUND
    }

    private final AbstractContainerScreen<?> screen;
    private final GuiGraphics graphics;
    private final int mouseX;
    private final int mouseY;
    private final float partialTick;
    private final Phase phase;

    public ContainerScreenEvent(
            AbstractContainerScreen<?> screen,
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick,
            Phase phase
    ) {
        this.screen = Objects.requireNonNull(screen, "screen");
        this.graphics = Objects.requireNonNull(graphics, "graphics");
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTick = partialTick;
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public AbstractContainerScreen<?> getScreen() {
        return this.screen;
    }

    public GuiGraphics getGraphics() {
        return this.graphics;
    }

    public int getMouseX() {
        return this.mouseX;
    }

    public int getMouseY() {
        return this.mouseY;
    }

    public float getPartialTick() {
        return this.partialTick;
    }

    public Phase getPhase() {
        return this.phase;
    }
}

package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Cancelable
public class ScreenEvent extends GeminiEvent {
    public enum Phase {
        INIT_PRE,
        INIT_POST,
        RENDER_PRE,
        RENDER_POST,
        OPENING,
        CLOSING
    }

    private final Minecraft client;
    private final @Nullable Screen screen;
    private final @Nullable GuiGraphics graphics;
    private final int mouseX;
    private final int mouseY;
    private final float partialTick;
    private final Phase phase;

    public ScreenEvent(
            Minecraft client,
            @Nullable Screen screen,
            @Nullable GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick,
            Phase phase
    ) {
        this.client = Objects.requireNonNull(client, "client");
        this.screen = screen;
        this.graphics = graphics;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTick = partialTick;
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public Minecraft getClient() {
        return this.client;
    }

    public @Nullable Screen getScreen() {
        return this.screen;
    }

    public @Nullable GuiGraphics getGraphics() {
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

package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public abstract class RenderGuiLayerEvent extends GeminiEvent {
    public enum Phase {
        PRE,
        POST
    }

    private final GuiGraphics graphics;
    private final ResourceLocation layer;
    private final DeltaTracker deltaTracker;
    private final Phase phase;

    protected RenderGuiLayerEvent(
            GuiGraphics graphics,
            ResourceLocation layer,
            DeltaTracker deltaTracker,
            Phase phase
    ) {
        this.graphics = Objects.requireNonNull(graphics, "graphics");
        this.layer = Objects.requireNonNull(layer, "layer");
        this.deltaTracker = Objects.requireNonNull(deltaTracker, "deltaTracker");
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public GuiGraphics getGraphics() {
        return this.graphics;
    }

    public ResourceLocation getLayer() {
        return this.layer;
    }

    public DeltaTracker getDeltaTracker() {
        return this.deltaTracker;
    }

    public Phase getPhase() {
        return this.phase;
    }

    @Cancelable
    public static final class Pre extends RenderGuiLayerEvent {
        public Pre(GuiGraphics graphics, ResourceLocation layer, DeltaTracker deltaTracker) {
            super(graphics, layer, deltaTracker, Phase.PRE);
        }
    }

    public static final class Post extends RenderGuiLayerEvent {
        public Post(GuiGraphics graphics, ResourceLocation layer, DeltaTracker deltaTracker) {
            super(graphics, layer, deltaTracker, Phase.POST);
        }
    }
}

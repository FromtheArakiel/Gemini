package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.renderer.texture.TextureAtlas;

import java.util.Objects;

public class TextureAtlasStitchedEvent extends GeminiEvent {
    private final TextureAtlas atlas;

    public TextureAtlasStitchedEvent(TextureAtlas atlas) {
        this.atlas = Objects.requireNonNull(atlas, "atlas");
    }

    public TextureAtlas getAtlas() {
        return this.atlas;
    }
}

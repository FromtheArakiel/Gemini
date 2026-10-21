package dev.arakiel.gemini.creative.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SectionAnimatedTextured extends SectionTextured {
    private int frames;
    private float frameTimeInMs = 200.0F;

    public SectionAnimatedTextured(ResourceLocation id) {
        super(id);
    }

    @Override
    public void render(GuiGraphics graphics, Font font, int topLeftX, int topLeftY) {
        int frame = frames <= 0 ? 0 : (int) ((System.currentTimeMillis() / (long) frameTimeInMs) % frames);
        graphics.blit(texture, topLeftX, topLeftY, horizontalSize, verticalSize, 0, frame * verticalSize,
                horizontalSize, verticalSize, horizontalSize, verticalSize * Math.max(1, frames));
        renderTitle(graphics, font, topLeftX, topLeftY);
    }

    public static SectionAnimatedTextured of(
            ResourceLocation id,
            Component title,
            int frames,
            float frameTimeInMs,
            int textColor,
            boolean textShadow,
            boolean collapsible,
            ConglomerateOfItems items
    ) {
        SectionAnimatedTextured section = new SectionAnimatedTextured(id);
        section.title = title;
        section.textColor = textColor;
        section.textShadow = textShadow;
        section.collapsible = collapsible;
        section.items = items;
        section.frames = frames;
        section.frameTimeInMs = frameTimeInMs;
        return section;
    }

    public SectionAnimatedTextured setFrames(int frames) {
        this.frames = frames;
        return this;
    }

    public SectionAnimatedTextured setFrameTimeInMs(float frameTimeInMs) {
        this.frameTimeInMs = frameTimeInMs;
        return this;
    }
}

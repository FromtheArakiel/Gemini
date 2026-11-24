package dev.arakiel.gemini.tab.client.internal;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.tab.TabPlacement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

record TabSprites(TabPlacement placement) {
    private static final String[] STATES = {"des_0", "des_1", "des_2", "sel_0", "sel_1", "sel_2"};

    int width() {
        return this.placement.width();
    }

    int height() {
        return this.placement.height();
    }

    void draw(GuiGraphics graphics, int x, int y, boolean selected, int index) {
        int state = index == 0 ? 0 : 1;
        if (selected) {
            state += 3;
        }
        ResourceLocation sprite = Gemini.id(this.folder() + "/" + STATES[state]);
        graphics.blitSprite(sprite, x, y, this.width(), this.height());
    }

    void drawIcon(GuiGraphics graphics, int x, int y, IconRenderer renderer) {
        int iconX = x;
        int iconY = y;
        switch (this.placement) {
            case ABOVE -> {
                iconX += 6;
                iconY += 9;
            }
            case BELOW -> {
                iconX += 6;
                iconY += 6;
            }
            case LEFT -> {
                iconX += 10;
                iconY += 5;
            }
            case RIGHT -> {
                iconX += 6;
                iconY += 5;
            }
        }
        renderer.render(graphics, iconX, iconY);
    }

    int x(int index) {
        return switch (this.placement) {
            case ABOVE, BELOW -> this.width() * index;
            case LEFT -> -this.width() + 4;
            case RIGHT -> -4;
        };
    }

    int y(int index) {
        return switch (this.placement) {
            case ABOVE -> -this.height() + 4;
            case BELOW -> -4;
            case LEFT, RIGHT -> this.height() * index;
        };
    }

    private String folder() {
        return switch (this.placement) {
            case ABOVE -> "up";
            case BELOW -> "bottom";
            case LEFT -> "left";
            case RIGHT -> "right";
        };
    }

    @FunctionalInterface
    interface IconRenderer {
        void render(GuiGraphics graphics, int x, int y);
    }
}

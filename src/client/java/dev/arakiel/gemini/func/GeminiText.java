package dev.arakiel.gemini.func;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GeminiText {
    private GeminiText() {
    }

    public static List<FormattedCharSequence> wrap(Font font, Component text, int width) {
        Objects.requireNonNull(font, "font");
        Objects.requireNonNull(text, "text");
        if (width <= 0) {
            throw new IllegalArgumentException("Text width must be positive");
        }
        return font.split(text, width);
    }

    public static List<FormattedCharSequence> wrap(Font font, List<Component> lines, int width) {
        List<FormattedCharSequence> result = new ArrayList<>();
        for (Component line : lines) {
            result.addAll(wrap(font, line, width));
        }
        return List.copyOf(result);
    }

    public static int draw(
            GuiGraphics graphics,
            Font font,
            Component text,
            int x,
            int y,
            int width,
            int color,
            boolean shadow
    ) {
        return drawLines(graphics, font, wrap(font, text, width), x, y, color, shadow);
    }

    public static int draw(
            GuiGraphics graphics,
            Font font,
            List<Component> lines,
            int x,
            int y,
            int width,
            int color,
            boolean shadow
    ) {
        return drawLines(graphics, font, wrap(font, lines, width), x, y, color, shadow);
    }

    public static int height(Font font, Component text, int width) {
        return wrap(font, text, width).size() * font.lineHeight;
    }

    private static int drawLines(
            GuiGraphics graphics,
            Font font,
            List<FormattedCharSequence> lines,
            int x,
            int y,
            int color,
            boolean shadow
    ) {
        int offset = 0;
        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, x, y + offset, color, shadow);
            offset += font.lineHeight;
        }
        return offset;
    }
}

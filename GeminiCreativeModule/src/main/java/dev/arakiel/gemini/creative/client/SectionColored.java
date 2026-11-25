package dev.arakiel.gemini.creative.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SectionColored extends AbstractSectionWithTitle<SectionColored> {
    private int verticalSize = 162;
    private int horizontalSize = 18;
    private int offsetX;
    private int offsetY;
    private int bannerColor = 0xFF45BB77;
    private int bannerBorderColor = brighten(bannerColor, 0.2F);

    public SectionColored(ResourceLocation id) {
        super(id);
    }

    public SectionColored(
            ResourceLocation id,
            Component title,
            int bannerColor,
            int textColor,
            int textOutline,
            boolean textShadow,
            boolean collapsible,
            int verticalSize,
            int horizontalSize,
            ConglomerateOfItems items
    ) {
        super(id);
        this.title = title;
        this.bannerColor = bannerColor;
        this.bannerBorderColor = brighten(bannerColor, 0.2F);
        this.textColor = textColor;
        this.textOutline = textOutline;
        this.textShadow = textShadow;
        this.collapsible = collapsible;
        this.verticalSize = verticalSize;
        this.horizontalSize = horizontalSize;
        this.items = items;
    }

    public SectionColored(ResourceLocation id, Component title, int bannerColor, int textColor, ConglomerateOfItems items) {
        this(id, title, bannerColor, textColor, 0, true, true, 162, 18, items);
    }

    @Override
    public void render(GuiGraphics graphics, Font font, int topLeftX, int topLeftY) {
        graphics.fill(topLeftX + offsetX, topLeftY + offsetY, topLeftX + 162 - offsetX, topLeftY + 18 - offsetY, bannerBorderColor);
        graphics.fill(topLeftX + offsetX + 1, topLeftY + offsetY + 1, topLeftX + 161 - offsetX, topLeftY + 17 - offsetY, bannerColor);
        super.render(graphics, font, topLeftX, topLeftY);
    }

    public SectionColored setBannerColor(int bannerColor) {
        this.bannerColor = bannerColor;
        this.bannerBorderColor = brighten(bannerColor, 0.2F);
        return this;
    }

    public SectionColored setBannerBorderColor(int bannerBorderColor) {
        this.bannerBorderColor = bannerBorderColor;
        return this;
    }

    public SectionColored setVerticalSize(int verticalSize) {
        this.verticalSize = verticalSize;
        return this;
    }

    public SectionColored setHorizontalSize(int horizontalSize) {
        this.horizontalSize = horizontalSize;
        return this;
    }

    public SectionColored setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public SectionColored setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public SectionColored setTextureInsideRow() {
        this.horizontalSize = 160;
        this.verticalSize = 16;
        this.offsetX = 1;
        this.offsetY = 1;
        return this;
    }

    private static int brighten(int argb, float amount) {
        int alpha = argb >> 24 & 0xFF;
        int red = (int) ((argb >> 16 & 0xFF) + (255 - (argb >> 16 & 0xFF)) * amount);
        int green = (int) ((argb >> 8 & 0xFF) + (255 - (argb >> 8 & 0xFF)) * amount);
        int blue = (int) ((argb & 0xFF) + (255 - (argb & 0xFF)) * amount);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }
}

package dev.arakiel.gemini.creative.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SectionTextured extends AbstractSectionWithTitle<SectionTextured> {
    protected ResourceLocation texture;
    protected int horizontalSize = 162;
    protected int verticalSize = 18;
    protected int textureOffsetX;
    protected int textureOffsetY;

    public SectionTextured(ResourceLocation id) {
        super(id);
        this.texture = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "textures/gui/fancy_tab_section/" + id.getPath() + ".png");
    }

    public SectionTextured(
            ResourceLocation id,
            Component title,
            ResourceLocation texture,
            int textColor,
            boolean textShadow,
            boolean collapsible,
            ConglomerateOfItems items
    ) {
        super(id);
        this.title = title;
        this.texture = texture;
        this.textColor = textColor;
        this.textShadow = textShadow;
        this.collapsible = collapsible;
        this.items = items;
    }

    @Override
    public void render(GuiGraphics graphics, Font font, int topLeftX, int topLeftY) {
        graphics.blit(texture, topLeftX + textureOffsetX, topLeftY + textureOffsetY, 0, 0, horizontalSize, verticalSize, horizontalSize, verticalSize);
        super.render(graphics, font, topLeftX, topLeftY);
    }

    public SectionTextured setTexture(ResourceLocation texture) {
        this.texture = ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), "textures/gui/fancy_tab_section/" + texture.getPath() + ".png");
        return this;
    }

    public SectionTextured setTextureRaw(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    @Override
    public SectionTextured setTitle(Component title) {
        this.title = title;
        return this;
    }

    public SectionTextured setHorizontalSize(int horizontalSize) {
        this.horizontalSize = horizontalSize;
        return this;
    }

    public SectionTextured setVerticalSize(int verticalSize) {
        this.verticalSize = verticalSize;
        return this;
    }

    public SectionTextured setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        return this;
    }

    public SectionTextured setTextureOffsetX(int textureOffsetX) {
        this.textureOffsetX = textureOffsetX;
        return this;
    }

    public SectionTextured setTextureOffsetY(int textureOffsetY) {
        this.textureOffsetY = textureOffsetY;
        return this;
    }

    public SectionTextured setTextureInsideRow() {
        this.horizontalSize = 160;
        this.verticalSize = 16;
        this.textureOffsetX = 1;
        this.textureOffsetY = 1;
        return this;
    }
}

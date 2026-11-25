package dev.arakiel.gemini.tab.client.internal;

import dev.arakiel.gemini.tab.TabDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public final class TabButton extends Button {
    private final TabNavigator navigator;
    private final TabDefinition definition;
    private final TabSprites sprites;
    private final Component title;
    private final boolean selected;
    private final int slot;
    private final int page;
    private final TabHoverAnimation hover = new TabHoverAnimation();

    TabButton(
            TabNavigator navigator,
            TabDefinition definition,
            TabSprites sprites,
            Component title,
            boolean selected,
            int slot,
            int page,
            int x,
            int y
    ) {
        super(x, y, sprites.width(), sprites.height(), title, button -> navigator.activate(definition), DEFAULT_NARRATION);
        this.navigator = navigator;
        this.definition = definition;
        this.sprites = sprites;
        this.title = title;
        this.selected = selected;
        this.slot = slot;
        this.page = page;
    }

    public boolean selected() {
        return this.selected;
    }

    int page() {
        return this.page;
    }

    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY) {
        float brightness = this.hover.brightness(this.isMouseOver(mouseX, mouseY), System.nanoTime());
        graphics.setColor(brightness, brightness, brightness, 1.0F);
        this.sprites.draw(graphics, this.getX(), this.getY(), this.selected, this.slot);
        this.sprites.drawIcon(graphics, this.getX(), this.getY(), this::renderIcon);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible && this.selected) {
            this.renderBackground(graphics, mouseX, mouseY);
        }
        if (this.visible && this.isHovered()) {
            graphics.renderTooltip(Minecraft.getInstance().font, this.title, mouseX, mouseY);
        }
    }

    private void renderIcon(GuiGraphics graphics, int x, int y) {
        if (this.definition == null) {
            graphics.renderFakeItem(Items.CRAFTING_TABLE.getDefaultInstance(), x, y);
        } else {
            this.definition.icon().render(graphics, x, y);
        }
    }
}

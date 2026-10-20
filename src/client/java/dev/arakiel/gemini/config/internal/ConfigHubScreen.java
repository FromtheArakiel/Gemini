package dev.arakiel.gemini.config.internal;

import dev.arakiel.gemini.api.GeminiConfig;
import dev.arakiel.gemini.config.api.ConfigHandle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class ConfigHubScreen extends Screen {
    private static final int PAGE_SIZE = 6;

    private final Screen parent;
    private final List<ConfigHandle<?>> configs;
    private int page;

    public ConfigHubScreen(Screen parent) {
        super(Component.translatableWithFallback("gemini.config.title", "Gemini Configs"));
        this.parent = parent;
        this.configs = List.copyOf(GeminiConfig.all());
    }

    @Override
    protected void init() {
        int buttonWidth = Math.min(260, width - 40);
        int start = page * PAGE_SIZE;
        int end = Math.min(configs.size(), start + PAGE_SIZE);
        for (int i = start; i < end; i++) {
            ConfigHandle<?> handle = configs.get(i);
            int y = 44 + (i - start) * 24;
            addRenderableWidget(Button.builder(
                    Component.literal(handle.id().toString()),
                    button -> minecraft.setScreen(new ConfigScreen(this, handle))
            ).bounds((width - buttonWidth) / 2, y, buttonWidth, 20).build());
        }

        int pages = Math.max(1, (configs.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int navY = height - 30;
        int navWidth = 80;
        addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            page = Math.max(0, page - 1);
            rebuildWidgets();
        }).bounds(width / 2 - navWidth - 20, navY, navWidth, 20).build()).active = page > 0;
        addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            page = Math.min(pages - 1, page + 1);
            rebuildWidgets();
        }).bounds(width / 2 + 20, navY, navWidth, 20).build()).active = page + 1 < pages;
        addRenderableWidget(Button.builder(
                Component.translatableWithFallback("gui.back", "Back"),
                button -> minecraft.setScreen(parent)
        ).bounds((width - 100) / 2, height - 54, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, title, width / 2, 16, 0xFFFFFF);
        if (configs.isEmpty()) {
            graphics.drawCenteredString(
                    font,
                    Component.translatableWithFallback("gemini.config.none", "No configs registered"),
                    width / 2,
                    height / 2 - 10,
                    0xA0A0A0
            );
        } else {
            int pages = Math.max(1, (configs.size() + PAGE_SIZE - 1) / PAGE_SIZE);
            graphics.drawCenteredString(
                    font,
                    Component.literal((page + 1) + " / " + pages),
                    width / 2,
                    height - 44,
                    0xA0A0A0
            );
        }
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}

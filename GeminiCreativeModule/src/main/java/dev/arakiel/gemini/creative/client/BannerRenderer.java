package dev.arakiel.gemini.creative.client;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.mixin.creative.ContainerScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class BannerRenderer {
    public static int CURRENT_ROW;
    public static ResourceLocation CURRENT_TAB;

    public static final int ROW_HEIGHT = 18;
    public static final int GRID_COLS = 9;
    public static final int GRID_X_OFFSET = 10;
    public static final int GRID_Y_OFFSET = 17;
    public static final int VISIBLE_ROWS = 5;
    public static final int BANNER_WIDTH = GRID_COLS * ROW_HEIGHT - 4;

    public static final ResourceLocation EXPANDED_BUTTON = Gemini.id("textures/gui/collapse_button/expanded_button.png");
    public static final ResourceLocation EXPANDED_BUTTON_HIGHLIGHT = Gemini.id("textures/gui/collapse_button/expanded_button_highlight.png");
    public static final ResourceLocation COLLAPSED_BUTTON = Gemini.id("textures/gui/collapse_button/collapsed_button.png");
    public static final ResourceLocation COLLAPSED_BUTTON_HIGHLIGHT = Gemini.id("textures/gui/collapse_button/collapsed_button_highlight.png");

    private BannerRenderer() {
    }

    public static void render(
            CreativeModeInventoryScreen screen,
            GuiGraphics graphics,
            List<Section<?>> sections,
            int mouseX,
            int mouseY
    ) {
        int left = ((ContainerScreenAccessor) screen).gemini$getLeftPos() + 8;
        int top = ((ContainerScreenAccessor) screen).gemini$getTopPos() + 17;
        Font font = Minecraft.getInstance().font;
        for (Section<?> section : sections) {
            int row = FancyTabSectionsInternals.getRowForSection(section);
            if (row == -1) {
                continue;
            }
            int relative = row - CURRENT_ROW;
            if (relative < 0 || relative >= VISIBLE_ROWS) {
                continue;
            }
            int y = top + relative * ROW_HEIGHT;
            section.render(graphics, font, left, y);
            boolean hoveringAny = sections.stream().anyMatch(other -> isInToggle(screen, other, mouseX, mouseY));
            section.renderToggle(screen, graphics, section, left, y, BANNER_WIDTH, ROW_HEIGHT, mouseX, mouseY, hoveringAny);
        }
    }

    public static boolean isInToggle(CreativeModeInventoryScreen screen, Section<?> section, double mouseX, double mouseY) {
        if (!section.collapsible()) {
            return false;
        }
        int row = FancyTabSectionsInternals.getRowForSection(section);
        if (row == -1) {
            return false;
        }
        int relative = row - CURRENT_ROW;
        if (relative < 0 || relative >= VISIBLE_ROWS) {
            return false;
        }
        int left = ((ContainerScreenAccessor) screen).gemini$getLeftPos() + GRID_X_OFFSET;
        int y = ((ContainerScreenAccessor) screen).gemini$getTopPos() + GRID_Y_OFFSET + relative * ROW_HEIGHT;
        int right = left + BANNER_WIDTH + 1;
        int toggleLeft = right - ROW_HEIGHT;
        return mouseX >= toggleLeft && mouseX < right && mouseY >= y && mouseY < y + ROW_HEIGHT - 1;
    }

    public static boolean isInBanner(CreativeModeInventoryScreen screen, double mouseX, double mouseY) {
        for (Section<?> section : FancyTabSections.getSections(CURRENT_TAB)) {
            int row = FancyTabSectionsInternals.getRowForSection(section);
            if (row == -1) {
                continue;
            }
            int relative = row - CURRENT_ROW;
            if (relative < 0 || relative >= VISIBLE_ROWS) {
                continue;
            }
            int left = ((ContainerScreenAccessor) screen).gemini$getLeftPos() + GRID_X_OFFSET;
            int y = ((ContainerScreenAccessor) screen).gemini$getTopPos() + GRID_Y_OFFSET + relative * ROW_HEIGHT;
            if (mouseX >= left - 2 && mouseX < left + BANNER_WIDTH + 1 && mouseY >= y && mouseY < y + ROW_HEIGHT - 1) {
                return true;
            }
        }
        return false;
    }
}

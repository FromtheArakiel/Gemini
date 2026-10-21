package dev.arakiel.gemini.creative.client;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.mixin.creative.ContainerScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class IndexPanel {
    private static final int ICON = 18;
    private static final int GAP = 2;
    private static final int PANEL_WIDTH = 24;
    private static final ResourceLocation TOGGLE_COLLAPSED = Gemini.id("textures/gui/index_panel/toggle_collapsed.png");
    private static final ResourceLocation TOGGLE_EXPANDED = Gemini.id("textures/gui/index_panel/toggle_expanded.png");
    private static final ResourceLocation SLOT = Gemini.id("textures/gui/index_panel/slot_border.png");
    private static final ResourceLocation SLOT_BOTTOM = Gemini.id("textures/gui/index_panel/slot_border_bottom.png");
    private static final ResourceLocation SLOT_TOP = Gemini.id("textures/gui/index_panel/slot_border_top.png");
    private static final int TOGGLE_LEFT = 6;
    private static final int TOGGLE_SIZE = 8;
    private static final int TOGGLE_TEXT_GAP = 3;
    private static final int TOGGLE_HIT_PAD = 3;
    private static final int HOVER = 0x60FFFFFF;
    private static final long ANIMATION_TIME = 250L;

    private static float panelScroll;
    private static long animationStart = -1L;
    private static float animationFrom;
    private static float animationTo;

    private IndexPanel() {
    }

    public static boolean active(List<Section<?>> sections) {
        return sections != null && !sections.isEmpty();
    }

    public static int titleX() {
        return TOGGLE_LEFT + TOGGLE_SIZE + TOGGLE_TEXT_GAP;
    }

    public static float tick() {
        if (animationStart < 0L) {
            return -1.0F;
        }
        long elapsed = System.currentTimeMillis() - animationStart;
        float progress = Mth.clamp(elapsed / (float) ANIMATION_TIME, 0.0F, 1.0F);
        float inverse = 1.0F - progress;
        float value = Mth.lerp(1.0F - inverse * inverse * inverse, animationFrom, animationTo);
        if (progress >= 1.0F) {
            animationStart = -1L;
        }
        return value;
    }

    public static void render(
            CreativeModeInventoryScreen screen,
            GuiGraphics graphics,
            List<Section<?>> sections,
            int mouseX,
            int mouseY
    ) {
        drawToggle(screen, graphics, mouseX, mouseY);
        if (!FancyTabSectionsConfig.indexExpanded() || sections.isEmpty()) {
            return;
        }

        int count = sections.size();
        panelScroll = Mth.clamp(panelScroll, 0.0F, maxScroll(screen, count));
        int x = panelX(screen);
        int y = panelY(screen);
        int height = panelHeight(screen, count);
        if (height <= 0) {
            return;
        }

        Component hoveredTitle = null;
        int slotHeight = ICON + GAP;
        int start = Math.max(0, (int) panelScroll / slotHeight);
        int visible = Math.min(6, count - start);
        for (int i = 0; i < visible; i++) {
            int index = start + i;
            int iconX = x + (PANEL_WIDTH - ICON) / 2;
            int iconY = y + 3 + i * slotHeight;
            boolean hovered = mouseX >= iconX && mouseX < iconX + ICON
                    && mouseY >= iconY && mouseY < iconY + ICON
                    && mouseY >= y && mouseY < y + height;
            ResourceLocation slot = i == 0 ? SLOT_TOP : i == visible - 1 ? SLOT_BOTTOM : SLOT;
            graphics.blit(slot, iconX - 3, iconY - 3, 24, 24, 24, 24, 24, 24, 24, 24);
            if (hovered) {
                graphics.fill(iconX + 1, iconY + 1, iconX + ICON - 1, iconY + ICON - 1, HOVER);
            }
            Section<?> section = sections.get(index);
            ItemStack icon = section.icon();
            graphics.renderItem(icon, iconX + 1, iconY + 1);
            graphics.renderItemDecorations(Minecraft.getInstance().font, icon, iconX + 1, iconY + 1);
            if (hovered) {
                hoveredTitle = section.title();
            }
        }
        if (hoveredTitle != null) {
            graphics.renderTooltip(Minecraft.getInstance().font, hoveredTitle, mouseX, mouseY);
        }
    }

    public static boolean mouseClicked(
            CreativeModeInventoryScreen screen,
            List<Section<?>> sections,
            float scrollOffset,
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button != 0) {
            return false;
        }
        int toggleX = ((ContainerScreenAccessor) screen).gemini$getLeftPos() + TOGGLE_LEFT;
        int toggleY = ((ContainerScreenAccessor) screen).gemini$getTopPos() + 6;
        if (mouseX >= toggleX - TOGGLE_HIT_PAD && mouseX < toggleX + TOGGLE_SIZE + TOGGLE_HIT_PAD
                && mouseY >= toggleY - TOGGLE_HIT_PAD && mouseY < toggleY + TOGGLE_SIZE + TOGGLE_HIT_PAD) {
            FancyTabSectionsConfig.setIndexExpanded(!FancyTabSectionsConfig.indexExpanded());
            panelScroll = 0.0F;
            return true;
        }
        if (!FancyTabSectionsConfig.indexExpanded() || sections.isEmpty()) {
            animationStart = -1L;
            return false;
        }

        int count = sections.size();
        int x = panelX(screen);
        int y = panelY(screen);
        int height = panelHeight(screen, count);
        if (height <= 0 || mouseX < x || mouseX >= x + PANEL_WIDTH || mouseY < y || mouseY >= y + height) {
            animationStart = -1L;
            return false;
        }
        int index = (int) ((mouseY - (y + 3) + panelScroll) / (ICON + GAP));
        if (index < 0 || index >= count) {
            return true;
        }
        int iconX = x + (PANEL_WIDTH - ICON) / 2;
        int iconY = y + 3 + index * (ICON + GAP) - (int) panelScroll;
        if (mouseX < iconX || mouseX >= iconX + ICON || mouseY < iconY || mouseY >= iconY + ICON) {
            return true;
        }
        jumpTo(screen, sections.get(index), scrollOffset);
        return true;
    }

    public static boolean mouseScrolled(
            CreativeModeInventoryScreen screen,
            List<Section<?>> sections,
            double mouseX,
            double mouseY,
            double scrollY
    ) {
        if (!FancyTabSectionsConfig.indexExpanded() || sections.isEmpty()) {
            animationStart = -1L;
            return false;
        }
        int count = sections.size();
        int x = panelX(screen);
        int y = panelY(screen);
        int height = panelHeight(screen, count);
        if (height <= 0 || mouseX < x || mouseX >= x + PANEL_WIDTH || mouseY < y || mouseY >= y + height) {
            animationStart = -1L;
            return false;
        }
        panelScroll = Mth.clamp((float) (panelScroll - scrollY * 20.0F), 0.0F, maxScroll(screen, count));
        return true;
    }

    private static void jumpTo(CreativeModeInventoryScreen screen, Section<?> section, float currentScroll) {
        int row = FancyTabSectionsInternals.getRowForSection(section);
        if (row == -1) {
            return;
        }
        int itemCount = screen.getMenu().items.size();
        int rows = Mth.positiveCeilDiv(itemCount, BannerRenderer.GRID_COLS) - BannerRenderer.VISIBLE_ROWS;
        if (rows <= 0) {
            return;
        }
        animationFrom = currentScroll;
        animationTo = Mth.clamp(row / (float) rows, 0.0F, 1.0F);
        animationStart = System.currentTimeMillis();
    }

    private static void drawToggle(CreativeModeInventoryScreen screen, GuiGraphics graphics, int mouseX, int mouseY) {
        int x = ((ContainerScreenAccessor) screen).gemini$getLeftPos() + TOGGLE_LEFT;
        int y = ((ContainerScreenAccessor) screen).gemini$getTopPos() + 6;
        boolean hovered = mouseX >= x && mouseX < x + TOGGLE_SIZE && mouseY >= y && mouseY < y + TOGGLE_SIZE;
        graphics.blit(FancyTabSectionsConfig.indexExpanded() ? TOGGLE_EXPANDED : TOGGLE_COLLAPSED,
                x, y, 0, 0, TOGGLE_SIZE, TOGGLE_SIZE, TOGGLE_SIZE, TOGGLE_SIZE);
        if (hovered) {
            graphics.fill(x, y, x + TOGGLE_SIZE, y + TOGGLE_SIZE, HOVER);
        }
    }

    private static int panelX(CreativeModeInventoryScreen screen) {
        return ((ContainerScreenAccessor) screen).gemini$getLeftPos() - 2 - PANEL_WIDTH;
    }

    private static int panelY(CreativeModeInventoryScreen screen) {
        return ((ContainerScreenAccessor) screen).gemini$getTopPos() + 4;
    }

    private static int visibleRows(CreativeModeInventoryScreen screen, int count) {
        int maxHeight = ((ContainerScreenAccessor) screen).gemini$getImageHeight() - 8;
        return Math.min(count, Math.max(0, (maxHeight - 6 + GAP) / (ICON + GAP)));
    }

    private static int panelHeight(CreativeModeInventoryScreen screen, int count) {
        int rows = visibleRows(screen, count);
        return rows <= 0 ? 0 : rows * (ICON + GAP) - GAP + 6;
    }

    private static float maxScroll(CreativeModeInventoryScreen screen, int count) {
        int contentHeight = count * (ICON + GAP) - GAP + 6;
        return Math.max(0, contentHeight - panelHeight(screen, count));
    }
}

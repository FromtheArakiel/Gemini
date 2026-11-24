package dev.arakiel.gemini.tab.client;

import dev.arakiel.gemini.tab.GeminiTabs;
import dev.arakiel.gemini.tab.TabContext;
import dev.arakiel.gemini.tab.TabLayout;
import dev.arakiel.gemini.tab.TabScreen;
import dev.arakiel.gemini.tab.client.internal.TabButton;
import dev.arakiel.gemini.tab.client.internal.TabNavigator;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public final class GeminiTabsClient {
    private GeminiTabsClient() {
    }

    public static void init() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!supports(screen)) {
                return;
            }
            TabContext context = new TabContext(screen, client.player);
            TabLayout layout = GeminiTabs.registry().layout(context);
            if (!layout.isEmpty()) {
                new TabNavigator(client, screen, layout).install();
            }
        });
    }

    public static void renderBackgrounds(GuiGraphics graphics, Screen screen, int mouseX, int mouseY) {
        for (GuiEventListener listener : screen.children()) {
            if (listener instanceof TabButton button && button.visible && !button.selected()) {
                button.renderBackground(graphics, mouseX, mouseY);
            }
        }
    }

    private static boolean supports(Screen screen) {
        return screen instanceof InventoryScreen
                || screen instanceof TabScreen && screen instanceof AbstractContainerScreen<?>;
    }

    static Minecraft minecraft() {
        return Minecraft.getInstance();
    }
}

package dev.arakiel.gemini.tab;

import dev.arakiel.gemini.Gemini;
import net.minecraft.resources.ResourceLocation;

public final class GeminiTabs {
    public static final ResourceLocation INVENTORY_ID = Gemini.id("inventory");
    public static final String INVENTORY_TRANSLATION_KEY = "menu.tabs.inventory";

    private static final TabRegistry REGISTRY = new TabRegistry();

    private GeminiTabs() {
    }

    public static TabRegistry registry() {
        return REGISTRY;
    }
}

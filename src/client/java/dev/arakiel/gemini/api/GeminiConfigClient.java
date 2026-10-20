package dev.arakiel.gemini.api;

import dev.arakiel.gemini.config.api.ConfigHandle;
import dev.arakiel.gemini.config.internal.ConfigHubScreen;
import dev.arakiel.gemini.config.internal.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public final class GeminiConfigClient {
    private GeminiConfigClient() {
    }

    public static Screen createScreen(Screen parent, ConfigHandle<?> handle) {
        return new ConfigScreen(parent, handle);
    }

    public static Screen createScreen(Screen parent, ResourceLocation id) {
        ConfigHandle<?> handle = GeminiConfig.get(id);
        if (handle == null) {
            throw new IllegalArgumentException("No config registered for " + id);
        }
        return createScreen(parent, handle);
    }

    public static Screen createHubScreen(Screen parent) {
        return new ConfigHubScreen(parent);
    }

    public static void open(Screen parent, ConfigHandle<?> handle) {
        Minecraft.getInstance().setScreen(createScreen(parent, handle));
    }

    public static void open(Screen parent, ResourceLocation id) {
        Minecraft.getInstance().setScreen(createScreen(parent, id));
    }

    public static void openHub(Screen parent) {
        Minecraft.getInstance().setScreen(createHubScreen(parent));
    }

    public static boolean hasConfigs() {
        return !GeminiConfig.all().isEmpty();
    }
}

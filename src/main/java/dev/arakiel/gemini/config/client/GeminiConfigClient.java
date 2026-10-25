package dev.arakiel.gemini.config.client;

import dev.arakiel.gemini.config.GeminiConfig;
import dev.arakiel.gemini.config.ConfigHandle;
import dev.arakiel.gemini.config.client.internal.ConfigHubScreen;
import dev.arakiel.gemini.config.client.internal.ConfigScreen;
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

    public static Screen createHubScreen(Screen parent, java.util.List<? extends ConfigHandle<?>> configs) {
        return new ConfigHubScreen(parent, configs);
    }

    public static void open(Screen parent, ConfigHandle<?> handle) {
        Screen screen = createScreen(parent, handle);
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(screen));
    }

    public static void open(Screen parent, ResourceLocation id) {
        Screen screen = createScreen(parent, id);
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(screen));
    }

    public static void openHub(Screen parent) {
        Screen screen = createHubScreen(parent);
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(screen));
    }

    public static void openHub(Screen parent, java.util.List<? extends ConfigHandle<?>> configs) {
        Screen screen = createHubScreen(parent, configs);
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(screen));
    }

    public static boolean hasConfigs() {
        return !GeminiConfig.all().isEmpty();
    }
}

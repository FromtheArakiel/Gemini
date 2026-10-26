package dev.arakiel.gemini.config.client;

import dev.arakiel.gemini.config.GeminiConfig;
import dev.arakiel.gemini.config.ConfigHandle;
import dev.arakiel.gemini.config.client.internal.ConfigHubScreen;
import dev.arakiel.gemini.config.client.internal.ConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public final class GeminiConfigClient {
    private static Screen pendingScreen;
    private static boolean initialized;

    private GeminiConfigClient() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Screen screen = pendingScreen;
            if (screen != null) {
                pendingScreen = null;
                client.setScreen(screen);
            }
        });
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
        pendingScreen = createScreen(parent, handle);
    }

    public static void open(Screen parent, ResourceLocation id) {
        pendingScreen = createScreen(parent, id);
    }

    public static void openHub(Screen parent) {
        pendingScreen = createHubScreen(parent);
    }

    public static void openHub(Screen parent, java.util.List<? extends ConfigHandle<?>> configs) {
        pendingScreen = createHubScreen(parent, configs);
    }

    public static boolean hasConfigs() {
        return !GeminiConfig.all().isEmpty();
    }
}

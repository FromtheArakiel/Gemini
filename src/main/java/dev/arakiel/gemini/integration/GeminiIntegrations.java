package dev.arakiel.gemini.integration;

import dev.arakiel.gemini.integration.internal.GeminiIntegrationManager;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Set;

public final class GeminiIntegrations {
    private static final GeminiIntegrationManager MANAGER = new GeminiIntegrationManager();

    private GeminiIntegrations() {
    }

    public static void initCommon() {
        MANAGER.initialize(GeminiIntegrationType.COMMON);
    }

    public static void initClient() {
        MANAGER.initialize(GeminiIntegrationType.CLIENT);
    }

    public static void initData() {
        MANAGER.initialize(GeminiIntegrationType.DATA);
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static boolean hasActiveIntegration(String modId) {
        return MANAGER.activeMods().contains(modId);
    }

    public static Set<String> activeMods() {
        return MANAGER.activeMods();
    }
}

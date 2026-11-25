package dev.arakiel.gemini.tab.client;

import net.fabricmc.api.ClientModInitializer;

public final class GeminiTabClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GeminiTabsClient.init();
    }
}

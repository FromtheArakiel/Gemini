package dev.arakiel.gemini.integration;

import net.fabricmc.api.ClientModInitializer;

public final class GeminiIntegrationClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GeminiIntegrations.initClient();
    }
}

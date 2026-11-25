package dev.arakiel.gemini.integration;

import net.fabricmc.api.ModInitializer;

public final class GeminiIntegrationInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        GeminiIntegrations.initCommon();
    }
}

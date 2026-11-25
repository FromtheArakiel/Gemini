package dev.arakiel.gemini.config.client;

import net.fabricmc.api.ClientModInitializer;

public final class GeminiConfigClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GeminiConfigClient.init();
        GeminiConfigCommand.init();
    }
}

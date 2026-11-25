package dev.arakiel.gemini.config;

import net.fabricmc.api.ModInitializer;

public final class GeminiConfigInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        GeminiConfig.init();
    }
}

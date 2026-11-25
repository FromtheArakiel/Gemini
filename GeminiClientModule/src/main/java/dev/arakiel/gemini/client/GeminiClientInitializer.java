package dev.arakiel.gemini.client;

import net.fabricmc.api.ClientModInitializer;

public final class GeminiClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GeminiClientEvents.init();
    }
}

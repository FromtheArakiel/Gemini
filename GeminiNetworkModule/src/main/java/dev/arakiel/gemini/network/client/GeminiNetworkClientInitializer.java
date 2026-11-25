package dev.arakiel.gemini.network.client;

import net.fabricmc.api.ClientModInitializer;

public final class GeminiNetworkClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GeminiNetworkClient.init();
    }
}

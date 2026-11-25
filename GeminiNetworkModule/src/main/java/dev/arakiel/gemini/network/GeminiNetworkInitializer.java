package dev.arakiel.gemini.network;

import net.fabricmc.api.ModInitializer;

public final class GeminiNetworkInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        GeminiNetwork.init();
    }
}

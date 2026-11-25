package dev.arakiel.gemini.event;

import net.fabricmc.api.ModInitializer;

public final class GeminiEventInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        GeminiEvents.init();
    }
}

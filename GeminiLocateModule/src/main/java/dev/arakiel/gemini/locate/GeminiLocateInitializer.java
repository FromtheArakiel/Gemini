package dev.arakiel.gemini.locate;

import net.fabricmc.api.ModInitializer;

public final class GeminiLocateInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        GeminiLocate.init();
    }
}

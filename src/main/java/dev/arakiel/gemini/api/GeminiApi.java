package dev.arakiel.gemini.api;

import dev.arakiel.gemini.api.combat.GeminiCombat;

public final class GeminiApi {
    private GeminiApi() {
    }

    public static void init() {
        GeminiCombat.init();
    }
}

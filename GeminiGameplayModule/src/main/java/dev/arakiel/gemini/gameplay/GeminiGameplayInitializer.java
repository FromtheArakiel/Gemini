package dev.arakiel.gemini.gameplay;

import dev.arakiel.gemini.attribute.GeminiAttributes;
import dev.arakiel.gemini.combat.internal.GeminiCombatImpl;
import net.fabricmc.api.ModInitializer;

public final class GeminiGameplayInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        GeminiAttributes.init();
        GeminiCombatImpl.init();
    }
}

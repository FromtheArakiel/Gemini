package dev.arakiel.gemini;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeminiIdentityTest {
    @Test
    void coreModuleUsesTheGeminiModId() {
        assertEquals("gemini", Gemini.MOD_ID);
    }
}

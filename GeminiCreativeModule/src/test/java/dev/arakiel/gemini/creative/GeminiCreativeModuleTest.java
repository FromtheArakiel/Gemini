package dev.arakiel.gemini.creative;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeminiCreativeModuleTest {
    @Test
    void creativeApiUsesTheGeminiCreativePackage() {
        assertEquals("dev.arakiel.gemini.creative", GeminiCreativeTabs.class.getPackageName());
    }
}

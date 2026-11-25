package dev.arakiel.gemini.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeminiClientModuleTest {
    @Test
    void clientBridgeLivesInTheClientApiPackage() {
        assertEquals("dev.arakiel.gemini.client", GeminiClientEvents.class.getPackageName());
    }
}

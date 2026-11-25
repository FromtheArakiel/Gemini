package dev.arakiel.gemini.integration.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiIntegrationVersionTest {
    @Test
    void mavenIntervalsAreTranslatedToFabricPredicates() {
        GeminiIntegrationVersion range = GeminiIntegrationVersion.parse("[19.0,)");

        assertTrue(range.matches("19.0"));
        assertTrue(range.matches("19.4.2"));
        assertFalse(range.matches("18.9"));
    }

    @Test
    void exclusiveBoundsAndExactVersionsArePreserved() {
        GeminiIntegrationVersion openRange = GeminiIntegrationVersion.parse("(1.0,2.0]");
        GeminiIntegrationVersion exact = GeminiIntegrationVersion.parse("1.2.3");

        assertFalse(openRange.matches("1.0"));
        assertTrue(openRange.matches("2.0"));
        assertFalse(openRange.matches("2.0.1"));
        assertTrue(exact.matches("1.2.3"));
        assertFalse(exact.matches("1.2.4"));
    }
}

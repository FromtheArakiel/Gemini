package dev.arakiel.gemini.integration.internal;

import dev.arakiel.gemini.integration.GeminiIntegration;
import dev.arakiel.gemini.integration.GeminiIntegrationType;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiIntegrationScannerTest {
    @Test
    void annotationDataIsReadWithoutConstructingTheIntegration() throws Exception {
        Path classFile = Path.of(GeminiIntegrationScannerTest.class
                .getResource("GeminiIntegrationScannerTest$GeminiIntegratedExample.class")
                .toURI());

        GeminiIntegrationEntry scanned = GeminiIntegrationScanner.scan(classFile).orElseThrow();

        assertEquals("example_mod", scanned.modId());
        assertEquals(GeminiIntegratedExample.class.getName(), scanned.className());
        assertEquals(List.of(GeminiIntegrationType.CLIENT, GeminiIntegrationType.DATA), scanned.types());
        assertTrue(scanned.version().matches("3.2.1"));
    }

    @GeminiIntegration(value = "example_mod", version = "[3.0,4.0)", type = {
            GeminiIntegrationType.CLIENT,
            GeminiIntegrationType.DATA
    })
    private static final class GeminiIntegratedExample {
        private GeminiIntegratedExample() {
            throw new AssertionError("Scanner must not construct integration classes");
        }

        public void applyClient() {
        }
    }
}

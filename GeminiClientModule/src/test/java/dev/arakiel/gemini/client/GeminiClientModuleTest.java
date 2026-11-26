package dev.arakiel.gemini.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiClientModuleTest {
    @Test
    void clientBridgeLivesInTheClientApiPackage() {
        assertEquals("dev.arakiel.gemini.client", GeminiClientEvents.class.getPackageName());
    }

    @Test
    void metadataDeclaresClientOnlyLifecycle() throws IOException {
        String metadata = resource("fabric.mod.json");
        assertTrue(metadata.contains("\"environment\": \"client\""));
        assertTrue(metadata.contains("\"client\": ["));
        assertTrue(metadata.contains("dev.arakiel.gemini.client.GeminiClientInitializer"));
    }

    private static String resource(String path) throws IOException {
        try (InputStream input = GeminiClientModuleTest.class.getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                throw new IOException("Missing resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

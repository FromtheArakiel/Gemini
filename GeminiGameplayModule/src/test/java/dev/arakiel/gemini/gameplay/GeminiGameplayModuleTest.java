package dev.arakiel.gemini.gameplay;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiGameplayModuleTest {
    @Test
    void metadataDeclaresSharedModuleWithMainEntrypoint() throws IOException {
        String metadata = resource("fabric.mod.json");
        assertTrue(metadata.contains("\"id\": \"gemini_gameplay\""));
        assertTrue(metadata.contains("\"environment\": \"*\""));
        assertTrue(metadata.contains("dev.arakiel.gemini.gameplay.GeminiGameplayInitializer"));
        assertTrue(metadata.contains("\"gemini_event\": "));
    }

    @Test
    void clientMixinIsIsolatedToClientConfiguration() throws IOException {
        String mixins = resource("gemini_gameplay.mixins.json");
        assertTrue(mixins.contains("\"client\": ["));
        assertTrue(mixins.contains("client.mixin.tweak.LocalPlayerMixin"));
        assertFalse(
                mixins.substring(mixins.indexOf("\"mixins\": ["), mixins.indexOf("\"client\": ["))
                        .contains("LocalPlayerMixin")
        );
    }

    @Test
    void clientMixinClassIsPackagedUnderClientNamespace() {
        assertNotNull(ClassLoader.getSystemResource(
                "dev/arakiel/gemini/client/mixin/tweak/LocalPlayerMixin.class"
        ));
    }

    private static String resource(String path) throws IOException {
        try (InputStream input = GeminiGameplayModuleTest.class.getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                throw new IOException("Missing resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

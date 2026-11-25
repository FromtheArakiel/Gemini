package dev.arakiel.gemini.config;

import dev.arakiel.gemini.config.internal.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class GeminiConfigTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void annotationDrivesTopLevelRegistration() {
        ConfigManager.configureRoot(temporaryDirectory);
        ConfigHandle<FeatureConfig> handle = GeminiConfig.register(FeatureConfig.class);

        assertEquals(ResourceLocation.fromNamespaceAndPath("gemini_test", "feature"), handle.id());
        assertEquals(temporaryDirectory.resolve("gemini_test-feature.toml"), handle.path());
        assertSame(handle, GeminiConfig.get(FeatureConfig.class));
        assertEquals(3, handle.value().level);
    }

    @Config(namespace = "gemini_test", value = "feature", nested = false)
    public static final class FeatureConfig {
        @ConfigEntry("feature.level")
        public int level = 3;
    }
}

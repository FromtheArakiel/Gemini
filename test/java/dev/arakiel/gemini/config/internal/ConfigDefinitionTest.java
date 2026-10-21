package dev.arakiel.gemini.config.internal;

import dev.arakiel.gemini.config.ConfigEntry;
import dev.arakiel.gemini.config.ConfigHandle;
import dev.arakiel.gemini.config.ConfigOption;
import dev.arakiel.gemini.config.Range;
import dev.arakiel.gemini.config.RestartRequired;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigDefinitionTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsTomlWithNestedTablesAndComments() throws Exception {
        Path file = temporaryDirectory.resolve("gemini").resolve("gameplay.toml");
        ConfigHandle<GameplayConfig> handle = definition("gameplay", file);

        assertTrue(Files.exists(file));
        String content = Files.readString(file);
        assertTrue(content.contains("[client]"));
        assertTrue(content.contains("Enable gameplay hooks"));
        assertEquals(5, handle.value().count);
        assertEquals(1.5D, handle.value().clientSpeed);
    }

    @Test
    void migrationPreservesOldValuesAndAddsNewOptions() throws Exception {
        Path file = temporaryDirectory.resolve("gemini").resolve("migration.toml");
        Files.createDirectories(file.getParent());
        Files.writeString(file, """
                enabled = false
                count = 12
                difficulty = "HARD"
                allowed = ["alpha", "beta"]
                [client]
                speed = 2.5
                """);

        GameplayConfig config = definition("migration", file).value();

        assertFalse(config.enabled);
        assertEquals(12, config.count);
        assertEquals(Difficulty.HARD, config.difficulty);
        assertEquals(List.of("alpha", "beta"), config.allowed);
        assertEquals(2.5D, config.clientSpeed);
        assertEquals(7, config.clientNewSetting);
        assertTrue(Files.readString(file).contains("new_setting = 7"));
    }

    @Test
    void invalidRangeIsCorrectedToDefault() throws Exception {
        Path file = temporaryDirectory.resolve("gemini").resolve("range.toml");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "count = 999\n");

        ConfigHandle<GameplayConfig> handle = definition("range", file);

        assertEquals(5, handle.value().count);
        assertTrue(Files.readString(file).contains("count = 5"));
    }

    @Test
    void stagedChangesOnlyPersistWhenSaved() throws Exception {
        Path file = temporaryDirectory.resolve("gemini").resolve("editing.toml");
        ConfigHandle<GameplayConfig> handle = definition("editing", file);

        handle.stage("count", 9);
        assertEquals(9, handle.value().count);

        ConfigHandle<GameplayConfig> reloaded = new ConfigDefinition<>(
                ResourceLocation.fromNamespaceAndPath("gemini_test", "editing_reload"),
                file,
                GameplayConfig.class
        );
        assertEquals(5, reloaded.value().count);

        handle.save();
        reloaded.reload();
        assertEquals(9, reloaded.value().count);
    }

    @Test
    void resetAndRestartTrackingWork() throws Exception {
        ConfigHandle<GameplayConfig> handle = definition(
                "restart",
                temporaryDirectory.resolve("gemini").resolve("restart.toml")
        );

        handle.stage("client.speed", 3.0D);
        assertTrue(handle.needsRestart());
        assertEquals(3.0D, handle.value().clientSpeed);
        handle.stage("client.speed", 1.5D);
        assertFalse(handle.needsRestart());
        handle.stage("client.speed", 3.0D);

        handle.reload();
        assertFalse(handle.needsRestart());
        assertEquals(1.5D, handle.value().clientSpeed);

        handle.stage("count", 20);
        handle.resetAll();
        assertEquals(5, handle.value().count);
        assertEquals(1.5D, handle.value().clientSpeed);
    }

    @Test
    void optionMetadataIsAvailableForGui() throws Exception {
        ConfigHandle<GameplayConfig> handle = definition(
                "metadata",
                temporaryDirectory.resolve("gemini").resolve("metadata.toml")
        );
        Map<String, ConfigOption> options = handle.options().stream()
                .collect(java.util.stream.Collectors.toMap(ConfigOption::path, option -> option));

        assertEquals(1.0D, options.get("count").min());
        assertEquals(20.0D, options.get("count").max());
        assertEquals(List.of("EASY", "NORMAL", "HARD"), options.get("difficulty").choices());
        assertTrue(options.get("client.speed").restartRequired());
    }

    @Test
    void nestedFolderCanBeDisabled() {
        ConfigManager.configureRoot(temporaryDirectory);
        ConfigHandle<GameplayConfig> nested = ConfigManager.register(
                ResourceLocation.fromNamespaceAndPath("gemini_test", "nested_path"),
                true,
                GameplayConfig.class
        );
        ConfigHandle<GameplayConfig> flat = ConfigManager.register(
                ResourceLocation.fromNamespaceAndPath("gemini_test", "flat_path"),
                false,
                GameplayConfig.class
        );

        assertEquals(
                temporaryDirectory.resolve("gemini_test").resolve("nested_path.toml"),
                nested.path()
        );
        assertEquals(temporaryDirectory.resolve("gemini_test-flat_path.toml"), flat.path());
    }

    private static ConfigHandle<GameplayConfig> definition(String name, Path file) {
        return new ConfigDefinition<>(
                ResourceLocation.fromNamespaceAndPath("gemini_test", name),
                file,
                GameplayConfig.class
        );
    }

    public static final class GameplayConfig {
        @ConfigEntry(comment = "Enable gameplay hooks")
        public boolean enabled = true;

        @ConfigEntry
        @Range(min = 1, max = 20)
        public int count = 5;

        @ConfigEntry
        public Difficulty difficulty = Difficulty.NORMAL;

        @ConfigEntry
        public List<String> allowed = List.of("alpha");

        @ConfigEntry("client.speed")
        @RestartRequired
        public double clientSpeed = 1.5D;

        @ConfigEntry("client.new_setting")
        public int clientNewSetting = 7;
    }

    public enum Difficulty {
        EASY,
        NORMAL,
        HARD
    }
}

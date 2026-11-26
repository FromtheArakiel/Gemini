package dev.arakiel.gemini.event;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuntimeEventCoverageTest {
    private static final Set<String> SUPPORT_TYPES = Set.of(
            "Cancelable",
            "EventPriority",
            "EventResult",
            "EventListener",
            "GeminiEvent",
            "GeminiEventBus",
            "GeminiEventInitializer",
            "GeminiEvents",
            "GeminiServerReloadBridge",
            "FurnaceBurnTimeEvent",
            "HasResult",
            "ServerEvent",
            "LivingEvent",
            "ServerLevelEvent",
            "SubscribeEvent",
            "Subscription"
    );

    @Test
    void everyEventHasACompiledOrBridgedRuntimeEntry() throws IOException {
        Path moduleRoot = locateModuleRoot();
        String runtimeSources = readTree(moduleRoot.resolve("src/main/java/dev/arakiel/gemini/mixin"))
                + readTree(moduleRoot.resolve("src/main/java/dev/arakiel/gemini/event/bridge"))
                + readTree(moduleRoot.resolve("src/main/java/dev/arakiel/gemini/event/resource"))
                + readTree(moduleRoot.getParent().resolve("GeminiClientModule/src/main/java"));

        try (Stream<Path> files = Files.walk(moduleRoot.resolve("src/main/java/dev/arakiel/gemini/event"))) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                String simpleName = file.getFileName().toString().replace(".java", "");
                if (SUPPORT_TYPES.contains(simpleName)) {
                    continue;
                }
                assertTrue(
                        runtimeSources.contains(simpleName),
                        simpleName + " has no runtime entry in a Mixin, Fabric bridge, or client bridge"
                );
            }
        }
    }

    @Test
    void generatedMixinConfigsUseOneEventNamedMixinPerSource() throws IOException {
        Path moduleRoot = locateModuleRoot();
        String mixins = Files.readString(moduleRoot.resolve("src/main/resources/gemini_event.mixins.json"));
        String clientMixins = Files.readString(
                moduleRoot.resolve("src/main/resources/gemini_event.client.mixins.json")
        );

        List.of(
                "LivingEntityMixin",
                "PlayerMixin",
                "ItemStackMixin",
                "ItemEntityMixin",
                "ExplosionEventMixin",
                "ServerPlayerEventMixin",
                "BlockItemMixin",
                "ServerPlayerGameModeMixin",
                "BoneMealItemMixin",
                "MobSpawnMixin",
                "ItemCooldownsMixin"
        ).forEach(name -> {
            assertFalse(mixins.contains(name), "legacy composite mixin remains in config: " + name);
            assertFalse(clientMixins.contains(name), "legacy composite client mixin remains: " + name);
        });

        List.of(
                "LivingIncomingDamageEventMixin",
                "LivingDamageEventMixin",
                "LivingBreatheEventMixin",
                "LivingDrownEventMixin",
                "LivingFallEventMixin",
                "LivingDropsEventMixin",
                "LivingExperienceDropEventMixin",
                "MobSpawnEventMixin",
                "FinalizeSpawnEventMixin",
                "GrindstoneTakeItemEventMixin",
                "PotionBrewEventMixin",
                "VillagerTradesEventMixin",
                "WandererTradesEventMixin"
        ).forEach(name -> assertTrue(mixins.contains(name), "missing runtime mixin: " + name));

        List.of(
                "RenderGuiEventMixin",
                "RenderGuiLayerEventMixin",
                "RenderTooltipEventMixin",
                "RecipesUpdatedEventMixin",
                "RegisterRecipeBookCategoriesEventMixin"
        ).forEach(name -> assertTrue(clientMixins.contains(name), "missing client runtime mixin: " + name));
    }

    private static String readTree(Path root) throws IOException {
        if (!Files.isDirectory(root)) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        try (Stream<Path> files = Files.walk(root)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                text.append(Files.readString(file)).append('\n');
            }
        }
        return text.toString();
    }

    private static Path locateModuleRoot() {
        Path root = Path.of("GeminiEventModule");
        if (Files.isDirectory(root)) {
            return root;
        }
        Path current = Path.of("").toAbsolutePath();
        if (Files.isDirectory(current.resolve("src/main/java/dev/arakiel/gemini/event"))) {
            return current;
        }
        throw new AssertionError("Cannot locate GeminiEventModule");
    }
}

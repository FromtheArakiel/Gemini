package dev.arakiel.gemini.locate;

import com.mojang.datafixers.util.Pair;
import dev.arakiel.gemini.config.ConfigHandle;
import dev.arakiel.gemini.config.GeminiConfig;
import dev.arakiel.gemini.locate.internal.LocateExecutor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class GeminiLocate {
    private static volatile LocateConfig config;
    private static boolean initialized;

    private GeminiLocate() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ConfigHandle<LocateConfig> handle = GeminiConfig.register(LocateConfig.class);
        config = handle.value();
        LocateExecutor.configure(config);
        handle.addListener(LocateExecutor::configure);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> LocateExecutor.start());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> LocateExecutor.stop());
    }

    public static LocateConfig config() {
        LocateConfig value = config;
        if (value == null) {
            throw new IllegalStateException("Gemini locate module is not initialized");
        }
        return value;
    }

    public static LocateTask<BlockPos> findStructure(
            ServerLevel level,
            TagKey<Structure> tag,
            BlockPos origin,
            int radius,
            boolean skipKnown
    ) {
        Objects.requireNonNull(tag, "tag");
        Optional<HolderSet.Named<Structure>> structures = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getTag(tag);
        if (structures.isEmpty()) {
            return LocateTask.completed(level.getServer(), null);
        }
        return findStructure(level, structures.get(), origin, radius, skipKnown)
                .map(pair -> pair == null ? null : pair.getFirst());
    }

    public static LocateTask<Pair<BlockPos, Holder<Structure>>> findStructure(
            ServerLevel level,
            HolderSet<Structure> structures,
            BlockPos origin,
            int radius,
            boolean skipKnown
    ) {
        return LocateExecutor.findStructures(
                level,
                Objects.requireNonNull(structures, "structures"),
                Objects.requireNonNull(origin, "origin").immutable(),
                radius,
                skipKnown
        );
    }

    public static LocateTask<Pair<BlockPos, Holder<Biome>>> findBiome(
            ServerLevel level,
            Predicate<Holder<Biome>> biomePredicate,
            BlockPos origin,
            int radius,
            int horizontalStep,
            int verticalStep
    ) {
        return LocateExecutor.findBiome(
                level,
                Objects.requireNonNull(biomePredicate, "biomePredicate"),
                Objects.requireNonNull(origin, "origin").immutable(),
                radius,
                horizontalStep,
                verticalStep
        );
    }

    public static boolean isLevelActive(ServerLevel level) {
        return level.getServer().isRunning()
                && level.getServer().getLevel(level.dimension()) == level;
    }
}

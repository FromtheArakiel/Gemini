package dev.arakiel.gemini.locate.internal;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class BiomeFinder {
    private BiomeFinder() {
    }

    @Nullable
    public static Pair<BlockPos, Holder<Biome>> findClosest(
            BlockPos origin,
            int radius,
            int horizontalStep,
            int verticalStep,
            Predicate<Holder<Biome>> predicate,
            Climate.Sampler sampler,
            LevelReader level,
            MultiNoiseBiomeSource source
    ) {
        Set<Holder<Biome>> targets = source.possibleBiomes().stream()
                .filter(predicate)
                .collect(Collectors.toUnmodifiableSet());
        if (targets.isEmpty()) {
            return null;
        }

        Climate.ParameterList<Holder<Biome>> parameters = MultiNoiseAccess.invokeParameters(source);
        BiomeEnvelopeSelector selector = new BiomeEnvelopeSelector(targets, parameters);
        int radiusSteps = Math.floorDiv(radius * 2, horizontalStep);

        for (int ring = 0; ring <= radiusSteps; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (ring > 0 && Math.abs(dx) != ring && Math.abs(dz) != ring) {
                        continue;
                    }
                    int x = origin.getX() + dx * horizontalStep;
                    int z = origin.getZ() + dz * horizontalStep;
                    BlockPos column = new BlockPos(x, 0, z);
                    BiomeNoiseChecker checker = new BiomeNoiseChecker(selector.combined(), column);
                    Pair<BlockPos, Holder<Biome>> result = checkColumn(
                            level, source, sampler, checker, x, z, origin.getY(), verticalStep, targets
                    );
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private static Pair<BlockPos, Holder<Biome>> checkColumn(
            LevelReader level,
            MultiNoiseBiomeSource source,
            Climate.Sampler sampler,
            BiomeNoiseChecker checker,
            int x,
            int z,
            int originY,
            int step,
            Set<Holder<Biome>> targets
    ) {
        int minY = level.getMinBuildHeight() + 1;
        int maxY = level.getMaxBuildHeight() - 1;
        int start = Math.max(minY, Math.min(maxY, originY));
        for (int direction = -1; direction <= 1; direction += 2) {
            int first = direction < 0 ? start - step : start;
            for (int y = first; y >= minY && y <= maxY; y += direction * step) {
                if (!checker.matches(sampler, y)) {
                    continue;
                }
                Holder<Biome> biome = source.getNoiseBiome(checker.target(sampler, y));
                if (targets.contains(biome)) {
                    BlockPos pos = new BlockPos(x, y, z);
                    return Pair.of(pos, biome);
                }
            }
            if (direction == 1 && start == 0) {
                break;
            }
        }
        return null;
    }

    private static final class MultiNoiseAccess {
        private static Climate.ParameterList<Holder<Biome>> invokeParameters(MultiNoiseBiomeSource source) {
            return ((dev.arakiel.gemini.mixin.locate.MultiNoiseBiomeSourceAccessor) (Object) source).gemini$parameters();
        }
    }
}

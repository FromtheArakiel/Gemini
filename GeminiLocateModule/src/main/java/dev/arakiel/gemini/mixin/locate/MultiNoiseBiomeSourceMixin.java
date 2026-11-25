package dev.arakiel.gemini.mixin.locate;

import com.mojang.datafixers.util.Pair;
import dev.arakiel.gemini.integration.GeminiIntegrations;
import dev.arakiel.gemini.locate.GeminiLocate;
import dev.arakiel.gemini.locate.internal.BiomeFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Predicate;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin extends BiomeSource {
    @Override
    @Nullable
    public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(
            BlockPos pos,
            int radius,
            int horizontalStep,
            int verticalStep,
            Predicate<Holder<Biome>> predicate,
            Climate.Sampler sampler,
            LevelReader level
    ) {
        if (!GeminiLocate.config().biomeFilter || GeminiIntegrations.isModLoaded("terrablender")) {
            return super.findClosestBiome3d(
                    pos,
                    radius,
                    horizontalStep,
                    verticalStep,
                    predicate,
                    sampler,
                    level
            );
        }
        return BiomeFinder.findClosest(
                pos,
                radius,
                horizontalStep,
                verticalStep,
                predicate,
                sampler,
                level,
                (MultiNoiseBiomeSource) (Object) this
        );
    }
}

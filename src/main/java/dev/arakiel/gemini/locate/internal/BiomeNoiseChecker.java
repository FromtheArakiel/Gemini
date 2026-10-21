package dev.arakiel.gemini.locate.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;

final class BiomeNoiseChecker {
    private final BiomeEnvelope envelope;
    private final int x;
    private final int z;

    BiomeNoiseChecker(BiomeEnvelope envelope, BlockPos pos) {
        this.envelope = envelope;
        this.x = pos.getX();
        this.z = pos.getZ();
    }

    boolean matches(Climate.Sampler sampler, int y) {
        if (envelope.empty()) {
            return false;
        }
        DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(x, y, z);
        return envelope.matches(
                Climate.quantizeCoord((float) sampler.temperature().compute(context)),
                Climate.quantizeCoord((float) sampler.humidity().compute(context)),
                Climate.quantizeCoord((float) sampler.continentalness().compute(context)),
                Climate.quantizeCoord((float) sampler.erosion().compute(context)),
                Climate.quantizeCoord((float) sampler.weirdness().compute(context))
        );
    }

    Climate.TargetPoint target(Climate.Sampler sampler, int y) {
        return sampler.sample(
                net.minecraft.core.QuartPos.fromBlock(x),
                net.minecraft.core.QuartPos.fromBlock(y),
                net.minecraft.core.QuartPos.fromBlock(z)
        );
    }
}

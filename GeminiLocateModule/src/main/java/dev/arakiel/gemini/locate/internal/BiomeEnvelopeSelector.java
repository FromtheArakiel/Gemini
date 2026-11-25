package dev.arakiel.gemini.locate.internal;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BiomeEnvelopeSelector {
    private final Map<Holder<Biome>, BiomeEnvelope> envelopes = new HashMap<>();
    private final BiomeEnvelope combined = new BiomeEnvelope();

    public BiomeEnvelopeSelector(
            Set<Holder<Biome>> biomes,
            Climate.ParameterList<Holder<Biome>> parameters
    ) {
        for (var pair : parameters.values()) {
            Holder<Biome> biome = pair.getSecond();
            if (biomes.contains(biome)) {
                envelopes.computeIfAbsent(biome, ignored -> new BiomeEnvelope()).add(pair.getFirst());
                combined.add(pair.getFirst());
            }
        }
    }

    public BiomeEnvelope combined() {
        return combined;
    }
}

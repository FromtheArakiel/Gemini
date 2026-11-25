package dev.arakiel.gemini.mixin.locate;

import com.mojang.datafixers.util.Pair;
import dev.arakiel.gemini.integration.GeminiIntegrations;
import dev.arakiel.gemini.locate.GeminiLocate;
import dev.arakiel.gemini.locate.internal.BiomeEnvelopeSelector;
import dev.arakiel.gemini.locate.internal.StructureFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorLocateMixin {
    @Inject(
            method = "getNearestGeneratedStructure(Ljava/util/Set;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/level/StructureManager;IIIZJLnet/minecraft/world/level/levelgen/structure/placement/RandomSpreadStructurePlacement;)Lcom/mojang/datafixers/util/Pair;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void gemini$filterStructureCandidates(
            Set<Holder<Structure>> structures,
            LevelReader level,
            StructureManager manager,
            int centerX,
            int centerZ,
            int radius,
            boolean skipKnown,
            long seed,
            RandomSpreadStructurePlacement placement,
            CallbackInfoReturnable<Pair<BlockPos, Holder<Structure>>> callback
    ) {
        if (!GeminiLocate.config().structureBiomeFilter || GeminiIntegrations.isModLoaded("terrablender")) {
            return;
        }
        StructureCheckAccessor check = (StructureCheckAccessor) (Object)
                ((StructureManagerAccessor) manager).gemini$structureCheck();
        BiomeSource source = check.gemini$biomeSource();
        if (!(source instanceof MultiNoiseBiomeSource multiNoise)) {
            return;
        }

        Climate.ParameterList<Holder<Biome>> parameters =
                ((MultiNoiseBiomeSourceAccessor) multiNoise).gemini$parameters();
        Map<Holder<Structure>, BiomeEnvelopeSelector> candidates = new HashMap<>();
        for (Holder<Structure> structure : structures) {
            Set<Holder<Biome>> biomes = structure.value().biomes().stream()
                    .collect(java.util.stream.Collectors.toUnmodifiableSet());
            candidates.put(structure, new BiomeEnvelopeSelector(biomes, parameters));
        }

        int spacing = placement.spacing();
        for (int offsetX = -radius; offsetX <= radius; offsetX++) {
            boolean edgeX = offsetX == -radius || offsetX == radius;
            for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                boolean edgeZ = offsetZ == -radius || offsetZ == radius;
                if (!edgeX && !edgeZ) {
                    continue;
                }
                ChunkPos candidate = placement.getPotentialStructureChunk(
                        seed,
                        centerX + spacing * offsetX,
                        centerZ + spacing * offsetZ
                );
                Pair<BlockPos, Holder<Structure>> found = StructureFinder.find(
                        candidates,
                        level,
                        manager,
                        skipKnown,
                        placement,
                        candidate,
                        parameters,
                        check.gemini$randomState().sampler()
                );
                if (found != null) {
                    callback.setReturnValue(found);
                    return;
                }
            }
        }
        callback.setReturnValue(null);
    }
}

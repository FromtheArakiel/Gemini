package dev.arakiel.gemini.locate.internal;

import com.mojang.datafixers.util.Pair;
import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.mixin.locate.StructureCheckAccessor;
import dev.arakiel.gemini.mixin.locate.StructureManagerAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class StructureFinder {
    private static final TagKey<Structure> UNINITIALIZED_SAFE = TagKey.create(
            net.minecraft.core.registries.Registries.STRUCTURE,
            Gemini.id("uninitialized_safe")
    );

    private StructureFinder() {
    }

    @Nullable
    public static Pair<BlockPos, Holder<Structure>> find(
            Map<Holder<Structure>, BiomeEnvelopeSelector> structures,
            LevelReader level,
            StructureManager manager,
            boolean skipKnown,
            RandomSpreadStructurePlacement placement,
            ChunkPos chunkPos,
            Climate.ParameterList<Holder<Biome>> parameters,
            Climate.Sampler sampler
    ) {
        StructureCheck check = ((StructureManagerAccessor) manager).gemini$structureCheck();
        StructureCheckAccessor accessor = (StructureCheckAccessor) (Object) check;
        if (!placement.applyAdditionalChunkRestrictions(chunkPos.x, chunkPos.z, accessor.gemini$seed())) {
            return null;
        }

        Object2IntMap<Structure> loaded = accessor.gemini$loadedChunks().get(chunkPos.toLong());
        for (var entry : structures.entrySet()) {
            Holder<Structure> holder = entry.getKey();
            StructureCheckResult presence;
            if (loaded != null) {
                int references = loaded.getOrDefault(holder.value(), -1);
                presence = references == -1 || skipKnown && references != 0
                        ? StructureCheckResult.START_NOT_PRESENT
                        : StructureCheckResult.START_PRESENT;
            } else {
                BlockPos probe = placement.getLocatePos(chunkPos);
                if (!new BiomeNoiseChecker(entry.getValue().combined(), probe).matches(sampler, probe.getY())) {
                    continue;
                }
                presence = manager.checkStructurePresence(chunkPos, holder.value(), placement, skipKnown);
            }

            if (presence == StructureCheckResult.START_NOT_PRESENT) {
                continue;
            }
            if (!skipKnown && presence == StructureCheckResult.START_PRESENT) {
                return Pair.of(placement.getLocatePos(chunkPos), holder);
            }
            if (!skipKnown && holder.is(UNINITIALIZED_SAFE)) {
                return Pair.of(placement.getLocatePos(chunkPos), holder);
            }

            ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
            StructureStart start = manager.getStartForStructure(
                    SectionPos.bottomOf(chunk),
                    holder.value(),
                    chunk
            );
            if (start != null && start.isValid() && (!skipKnown || addReference(manager, start))) {
                return Pair.of(placement.getLocatePos(start.getChunkPos()), holder);
            }
        }
        return null;
    }

    private static boolean addReference(StructureManager manager, StructureStart start) {
        synchronized (start) {
            if (!start.canBeReferenced()) {
                return false;
            }
            manager.addReference(start);
            return true;
        }
    }
}

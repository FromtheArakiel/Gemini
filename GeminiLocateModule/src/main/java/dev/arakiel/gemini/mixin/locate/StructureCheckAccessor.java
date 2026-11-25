package dev.arakiel.gemini.mixin.locate;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureCheck.class)
public interface StructureCheckAccessor {
    @Accessor("randomState")
    RandomState gemini$randomState();

    @Accessor("biomeSource")
    BiomeSource gemini$biomeSource();

    @Accessor("seed")
    long gemini$seed();

    @Accessor("loadedChunks")
    Long2ObjectMap<Object2IntMap<Structure>> gemini$loadedChunks();
}

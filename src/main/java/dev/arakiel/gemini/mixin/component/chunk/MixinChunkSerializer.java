package dev.arakiel.gemini.mixin.component.chunk;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkSerializer.class})
public abstract class MixinChunkSerializer {
   @Inject(
      method = {"read"},
      at = {@At("RETURN")}
   )
   private static void deserialize(
      ServerLevel world, PoiManager poiStorage, RegionStorageInfo key, ChunkPos chunkPos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir
   ) {
      ProtoChunk ret = (ProtoChunk)cir.getReturnValue();
      ChunkAccess chunk = (ChunkAccess)(ret instanceof ImposterProtoChunk ? ((ImposterProtoChunk)ret).getWrapped() : ret);
      chunk.asComponentProvider().getComponentContainer().fromTag(tag, world.registryAccess());
      if (tag.contains("gemini_components", 10)) {
         int remainingComponentCount = tag.getCompound("gemini_components").size();
         chunk.setUnsaved(remainingComponentCount > 0);
      }
   }

   @Inject(
      method = {"write"},
      at = {@At("RETURN")}
   )
   private static void serialize(ServerLevel world, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir) {
      CompoundTag ret = (CompoundTag)cir.getReturnValue();
      chunk.asComponentProvider().getComponentContainer().toTag(ret, world.registryAccess());
   }
}

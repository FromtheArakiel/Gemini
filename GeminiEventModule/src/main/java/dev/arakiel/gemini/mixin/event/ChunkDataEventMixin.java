package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.chunk.ChunkDataEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public abstract class ChunkDataEventMixin {
    @Inject(method = "read", at = @At("HEAD"), cancellable = true)
    private static void gemini$loadChunkData(
            ServerLevel level,
            PoiManager poiManager,
            RegionStorageInfo storageInfo,
            ChunkPos pos,
            CompoundTag tag,
            CallbackInfoReturnable<ProtoChunk> callback
    ) {
        ChunkDataEvent event = new ChunkDataEvent(level, pos, tag, ChunkDataEvent.Phase.LOAD);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(null);
        }
    }

    @Inject(method = "write", at = @At("RETURN"))
    private static void gemini$saveChunkData(
            ServerLevel level,
            ChunkAccess chunk,
            CallbackInfoReturnable<CompoundTag> callback
    ) {
        GeminiEvents.post(new ChunkDataEvent(
                level,
                chunk.getPos(),
                callback.getReturnValue(),
                ChunkDataEvent.Phase.SAVE
        ));
    }
}

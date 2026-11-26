package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.chunk.ChunkWatchEvent;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public abstract class ChunkWatchEventMixin {
    @Inject(
            method = "markChunkPendingToSend(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/chunk/LevelChunk;)V",
            at = @At("HEAD")
    )
    private static void gemini$chunkWatch(ServerPlayer player, LevelChunk chunk, CallbackInfo callback) {
        GeminiEvents.post(new ChunkWatchEvent(
                player.serverLevel(),
                player,
                chunk.getPos(),
                ChunkWatchEvent.Phase.WATCH
        ));
    }

    @Inject(method = "dropChunk", at = @At("HEAD"))
    private static void gemini$chunkUnwatch(ServerPlayer player, ChunkPos pos, CallbackInfo callback) {
        GeminiEvents.post(new ChunkWatchEvent(
                player.serverLevel(),
                player,
                pos,
                ChunkWatchEvent.Phase.UNWATCH
        ));
    }
}

package dev.arakiel.gemini.mixin.component.block;

import dev.arakiel.gemini.component.ComponentCallbacks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerChunkSender.class})
public abstract class MixinChunkDataSender {
   @Inject(
      method = {"sendChunk"},
      at = {@At("RETURN")}
   )
   private static void sendChunkDataPackets(ServerGamePacketListenerImpl handler, ServerLevel world, LevelChunk chunk, CallbackInfo ci) {
      for (BlockEntity be : chunk.getBlockEntities().values()) {
         ((ComponentCallbacks.BlockEntitySync)ComponentCallbacks.BlockEntitySync.EVENT.invoker()).onBlockEntitySync(handler.player, be);
      }
   }
}

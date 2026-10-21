package dev.arakiel.gemini.mixin.component.block;

import dev.arakiel.gemini.component.ComponentCallbacks;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ChunkHolder.class})
public abstract class MixinChunkHolder {
   @ModifyVariable(
      method = {"broadcastBlockEntity"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/entity/BlockEntity;getUpdatePacket()Lnet/minecraft/network/protocol/Packet;"
      )}
   )
   private BlockEntity sendBlockEntityUpdate(BlockEntity be) {
      ((ComponentCallbacks.BlockEntitySyncAround)ComponentCallbacks.BlockEntitySyncAround.EVENT.invoker()).onBlockEntitySync(be);
      return be;
   }
}

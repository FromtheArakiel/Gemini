package dev.arakiel.gemini.mixin.component.block;

import dev.arakiel.gemini.component.block.BlockEntitySyncCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerPlayer.class})
public abstract class MixinServerPlayerEntity {
   @Inject(
      method = {"openCommandBlock"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/game/ClientboundBlockEntityDataPacket;create(Lnet/minecraft/world/level/block/entity/BlockEntity;Ljava/util/function/BiFunction;)Lnet/minecraft/network/protocol/game/ClientboundBlockEntityDataPacket;"
      )}
   )
   private void syncBlockEntity(CommandBlockEntity commandBlock, CallbackInfo ci) {
      ((BlockEntitySyncCallback)BlockEntitySyncCallback.EVENT.invoker()).onBlockEntitySync((ServerPlayer)(Object)this, commandBlock);
   }
}

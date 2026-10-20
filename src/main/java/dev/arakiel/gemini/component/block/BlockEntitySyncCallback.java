package dev.arakiel.gemini.component.block;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface BlockEntitySyncCallback {
   Event<BlockEntitySyncCallback> EVENT = EventFactory.createArrayBacked(BlockEntitySyncCallback.class, (BlockEntitySyncCallback)(p, be) -> {
   }, listeners -> (player, be) -> {
         for (BlockEntitySyncCallback callback : listeners) {
            callback.onBlockEntitySync(player, be);
         }
      });

   void onBlockEntitySync(ServerPlayer var1, BlockEntity var2);
}

package dev.arakiel.gemini.component.block;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface BlockEntitySyncAroundCallback {
   Event<BlockEntitySyncAroundCallback> EVENT = EventFactory.createArrayBacked(BlockEntitySyncAroundCallback.class, (BlockEntitySyncAroundCallback)be -> {
   }, listeners -> be -> {
         for (BlockEntitySyncAroundCallback callback : listeners) {
            callback.onBlockEntitySync(be);
         }
      });

   void onBlockEntitySync(BlockEntity var1);
}

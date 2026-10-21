package dev.arakiel.gemini.component.internal.block;

import dev.arakiel.gemini.component.ComponentCallbacks;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.block.StaticBlockComponentPlugin;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents.Load;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents.Unload;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class GeminiBlockComponents {
   public static final Type<ComponentUpdatePayload<BlockEntityAddress>> PACKET_ID = ComponentUpdatePayload.id("block_entity_sync");

   public static void init() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentUpdatePayload.register(PACKET_ID, BlockEntityAddress.CODEC);
         ComponentCallbacks.BlockEntitySync.EVENT.register((ComponentCallbacks.BlockEntitySync)(player, tracked) -> {
            ComponentProvider provider = (ComponentProvider)tracked;

            for (ComponentKey<?> key : provider.getComponentContainer().keys()) {
               key.syncWith(player, provider);
            }
         });
         ComponentCallbacks.BlockEntitySyncAround.EVENT.register((ComponentCallbacks.BlockEntitySyncAround)tracked -> {
            for (ComponentKey<?> key : ((ComponentProvider)tracked).getComponentContainer().keys()) {
               tracked.syncComponent(key);
            }
         });
      }

      if (FabricLoader.getInstance().isModLoaded("fabric-lifecycle-events-v1")) {
         ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((Load)(be, world) -> ((ComponentProvider)be).getComponentContainer().onServerLoad());
         ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((Unload)(be, world) -> ((ComponentProvider)be).getComponentContainer().onServerUnload());
      }

      StaticBlockComponentPlugin.INSTANCE.ensureInitialized();
   }
}
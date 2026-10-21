package dev.arakiel.gemini.component.internal.chunk;

import dev.arakiel.gemini.component.api.ComponentCallbacks;
import dev.arakiel.gemini.component.api.ComponentKey;
import dev.arakiel.gemini.component.api.ComponentProvider;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.network.MorePacketCodecs;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.Load;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.Unload;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.level.ChunkPos;

public final class GeminiChunkComponents {
   public static final Type<ComponentUpdatePayload<ChunkPos>> PACKET_ID = ComponentUpdatePayload.id("chunk_sync");

   public static void init() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentUpdatePayload.register(PACKET_ID, MorePacketCodecs.CHUNKPOS);
         ComponentCallbacks.ChunkSync.EVENT.register((ComponentCallbacks.ChunkSync)(player, tracked) -> {
            for (ComponentKey<?> key : tracked.asComponentProvider().getComponentContainer().keys()) {
               key.syncWith(player, (ComponentProvider)tracked);
            }
         });
      }

      if (FabricLoader.getInstance().isModLoaded("fabric-lifecycle-events-v1")) {
         ServerChunkEvents.CHUNK_LOAD.register((Load)(world, chunk) -> ((ComponentProvider)chunk).getComponentContainer().onServerLoad());
         ServerChunkEvents.CHUNK_UNLOAD.register((Unload)(world, chunk) -> ((ComponentProvider)chunk).getComponentContainer().onServerUnload());
      }

      StaticChunkComponentPlugin.INSTANCE.ensureInitialized();
   }
}

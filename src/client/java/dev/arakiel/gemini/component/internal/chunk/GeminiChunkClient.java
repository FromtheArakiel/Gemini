package dev.arakiel.gemini.component.internal.chunk;

import dev.arakiel.gemini.component.api.Component;
import dev.arakiel.gemini.component.api.ComponentProvider;
import dev.arakiel.gemini.component.internal.network.ComponentClientInternals;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Load;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Unload;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.ChunkPos;

public class GeminiChunkClient {
   public static void initClient() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentClientInternals.registerComponentSync(
            GeminiChunkComponents.PACKET_ID,
            (payload, ctx) -> payload.componentKey()
                  .flatMap(
                     key -> (Optional<? extends Component>)key.maybeGet(
                           Objects.requireNonNull(ctx.client().level).getChunk(((ChunkPos)payload.targetData()).x, ((ChunkPos)payload.targetData()).z)
                        )
                  )
         );
      }

      if (FabricLoader.getInstance().isModLoaded("fabric-lifecycle-events-v1")) {
         ClientChunkEvents.CHUNK_LOAD.register((Load)(world, chunk) -> ((ComponentProvider)chunk).getComponentContainer().onClientLoad());
         ClientChunkEvents.CHUNK_UNLOAD.register((Unload)(world, chunk) -> ((ComponentProvider)chunk).getComponentContainer().onClientUnload());
      }
   }
}

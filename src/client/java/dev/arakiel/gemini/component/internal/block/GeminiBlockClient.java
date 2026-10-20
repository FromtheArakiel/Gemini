package dev.arakiel.gemini.component.internal.block;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.internal.network.ComponentClientInternals;
import java.util.Optional;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents.Load;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents.Unload;
import net.fabricmc.loader.api.FabricLoader;

public class GeminiBlockClient {
   public static void initClient() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentClientInternals.registerComponentSync(
            GeminiBlockComponents.PACKET_ID,
            (payload, ctx) -> payload.componentKey()
                  .flatMap(
                     key -> (Optional<? extends Component>)key.maybeGet(
                           payload.targetData().beType().getBlockEntity(ctx.client().level, payload.targetData().bePos())
                        )
                  )
         );
      }

      if (FabricLoader.getInstance().isModLoaded("fabric-lifecycle-events-v1")) {
         ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((Load)(be, world) -> ((ComponentProvider)be).getComponentContainer().onServerLoad());
         ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((Unload)(be, world) -> ((ComponentProvider)be).getComponentContainer().onServerUnload());
      }
   }
}

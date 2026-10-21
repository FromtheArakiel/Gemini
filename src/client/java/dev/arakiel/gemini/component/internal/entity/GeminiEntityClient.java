package dev.arakiel.gemini.component.internal.entity;

import dev.arakiel.gemini.component.api.Component;
import dev.arakiel.gemini.component.api.ComponentProvider;
import dev.arakiel.gemini.component.internal.network.ComponentClientInternals;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents.Load;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents.Unload;
import net.fabricmc.loader.api.FabricLoader;

public final class GeminiEntityClient {
   public static void initClient() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentClientInternals.registerComponentSync(
            GeminiEntityComponents.PACKET_ID,
            (payload, ctx) -> payload.componentKey()
                  .flatMap(
                     key -> (Optional<? extends Component>)key.maybeGet(Objects.requireNonNull(ctx.client().level).getEntity((Integer)payload.targetData()))
                  )
         );
      }

      if (FabricLoader.getInstance().isModLoaded("fabric-lifecycle-events-v1")) {
         ClientEntityEvents.ENTITY_LOAD.register((Load)(entity, world) -> ((ComponentProvider)entity).getComponentContainer().onClientLoad());
         ClientEntityEvents.ENTITY_UNLOAD.register((Unload)(entity, world) -> ((ComponentProvider)entity).getComponentContainer().onClientUnload());
      }
   }
}

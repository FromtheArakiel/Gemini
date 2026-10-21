package dev.arakiel.gemini.component.internal.world;

import dev.arakiel.gemini.component.api.Component;
import dev.arakiel.gemini.component.internal.network.ComponentClientInternals;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;

public final class GeminiWorldClient {
   public static void initClient() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentClientInternals.registerComponentSync(
            GeminiWorldComponents.PACKET_ID,
            (payload, ctx) -> payload.componentKey().flatMap(key -> (Optional<? extends Component>)key.maybeGet(ctx.client().level))
         );
      }
   }
}

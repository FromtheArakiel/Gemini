package dev.arakiel.gemini.component.internal.level;

import dev.arakiel.gemini.component.api.Component;
import dev.arakiel.gemini.component.internal.network.ComponentClientInternals;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;

public final class GeminiLevelClient {
   public static void initClient() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentClientInternals.registerComponentSync(
            GeminiLevelComponents.PACKET_ID,
            (payload, ctx) -> payload.componentKey()
                  .flatMap(key -> (Optional<? extends Component>)key.maybeGet(Objects.requireNonNull(ctx.client().level).getLevelData()))
         );
      }
   }
}

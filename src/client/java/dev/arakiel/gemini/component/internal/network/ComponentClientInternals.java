package dev.arakiel.gemini.component.internal.network;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.sync.AutoSyncedComponent;
import dev.arakiel.gemini.component.internal.base.ComponentsInternals;
import dev.arakiel.gemini.component.internal.base.UnknownComponentException;
import java.util.Optional;
import java.util.function.BiFunction;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public final class ComponentClientInternals {
   public static <T extends ComponentUpdatePayload<?>> void registerComponentSync(
      Type<T> packetId, BiFunction<T, Context, Optional<? extends Component>> getter
   ) {
      ClientPlayNetworking.registerGlobalReceiver(
         packetId,
         (payload, ctx) -> {
            try {
               getter.apply((T)payload, ctx).ifPresent(c -> {
                  if (c instanceof AutoSyncedComponent synced) {
                     synced.applySyncPacket(payload.buf());
                  }
               });
            } catch (UnknownComponentException var7) {
               ctx.player()
                  .connection
                  .onDisconnect(
                     new DisconnectionDetails(
                        net.minecraft.network.chat.Component.literal(
                           var7.getMessage() + "\n(you are probably missing a mod installed on the server)" + ComponentsInternals.getClientOptionalModAdvice()
                        )
                     )
                  );
            } finally {
               payload.buf().release();
            }
         }
      );
   }
}

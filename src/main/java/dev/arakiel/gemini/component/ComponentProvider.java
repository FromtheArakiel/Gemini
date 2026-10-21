package dev.arakiel.gemini.component;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface ComponentProvider extends ComponentAccess {
   ComponentContainer getComponentContainer();

   default Iterable<ServerPlayer> getRecipientsForComponentSync() {
      return List.of();
   }

   @Nullable
   default <C extends ComponentSync.AutoSyncedComponent> CustomPacketPayload toComponentPacket(ComponentKey<? super C> key, boolean required, RegistryFriendlyByteBuf data) {
      return null;
   }
}
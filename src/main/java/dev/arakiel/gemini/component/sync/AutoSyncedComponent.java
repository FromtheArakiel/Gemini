package dev.arakiel.gemini.component.sync;

import dev.arakiel.gemini.component.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Contract;

public interface AutoSyncedComponent extends Component, ComponentPacketWriter, PlayerSyncPredicate {
   @Override
   default boolean shouldSyncWith(ServerPlayer player) {
      return true;
   }

   @Contract(
      mutates = "param1"
   )
   @Override
   default void writeSyncPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient) {
      CompoundTag tag = new CompoundTag();
      this.writeToNbt(tag, buf.registryAccess());
      buf.writeNbt(tag);
   }

   default void applySyncPacket(RegistryFriendlyByteBuf buf) {
      CompoundTag tag = buf.readNbt();
      if (tag != null) {
         this.readFromNbt(tag, buf.registryAccess());
      }
   }
}

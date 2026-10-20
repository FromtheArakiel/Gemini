package dev.arakiel.gemini.component.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Contract;

@FunctionalInterface
public interface ComponentPacketWriter {
   @Contract(
      mutates = "param1"
   )
   void writeSyncPacket(RegistryFriendlyByteBuf var1, ServerPlayer var2);
}

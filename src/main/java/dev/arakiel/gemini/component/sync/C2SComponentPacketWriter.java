package dev.arakiel.gemini.component.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.ApiStatus.Experimental;

@FunctionalInterface
@Experimental
public interface C2SComponentPacketWriter {
   C2SComponentPacketWriter EMPTY = buf -> {
   };

   @Contract(
      mutates = "param"
   )
   void writeC2SPacket(RegistryFriendlyByteBuf var1);
}

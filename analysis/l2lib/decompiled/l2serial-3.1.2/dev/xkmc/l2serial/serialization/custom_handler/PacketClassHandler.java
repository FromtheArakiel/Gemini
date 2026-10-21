package dev.xkmc.l2serial.serialization.custom_handler;

import net.minecraft.network.RegistryFriendlyByteBuf;

public interface PacketClassHandler<T> {
   void toPacket(RegistryFriendlyByteBuf var1, Object var2);

   T fromPacket(RegistryFriendlyByteBuf var1);
}

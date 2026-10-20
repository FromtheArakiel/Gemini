package dev.arakiel.gemini.component.internal.network;

import com.mojang.datafixers.util.Unit;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;

public final class MorePacketCodecs {
   public static final StreamCodec<ByteBuf, Unit> EMPTY = StreamCodec.unit(Unit.INSTANCE);
   public static final StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> REG_BYTE_BUF = StreamCodec.of((buf, value) -> {
      buf.writeVarInt(value.readableBytes());
      buf.writeBytes(value);
   }, buf -> {
      int readableBytes = buf.readVarInt();
      ByteBuf copy = Unpooled.buffer(readableBytes, readableBytes);
      buf.readBytes(copy, readableBytes);
      return new RegistryFriendlyByteBuf(copy, buf.registryAccess());
   });
   public static final StreamCodec<FriendlyByteBuf, ChunkPos> CHUNKPOS = StreamCodec.of(FriendlyByteBuf::writeChunkPos, FriendlyByteBuf::readChunkPos);
}

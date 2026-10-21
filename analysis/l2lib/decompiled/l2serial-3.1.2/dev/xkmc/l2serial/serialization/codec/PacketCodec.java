package dev.xkmc.l2serial.serialization.codec;

import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.serialization.unified_processor.PacketContext;
import dev.xkmc.l2serial.serialization.unified_processor.UnifiedCodec;
import dev.xkmc.l2serial.util.Wrappers;
import io.netty.buffer.Unpooled;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.connection.ConnectionType;

public class PacketCodec {
   public static RegistryFriendlyByteBuf unit(RegistryAccess access) {
      return new RegistryFriendlyByteBuf(Unpooled.buffer(), access, ConnectionType.NEOFORGE);
   }

   public static RegistryFriendlyByteBuf decode(RegistryAccess access, byte[] arr) {
      return new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(arr), access, ConnectionType.NEOFORGE);
   }

   public static byte[] encode(FriendlyByteBuf buf) {
      byte[] arr = new byte[buf.writerIndex()];
      buf.getBytes(0, arr);
      return arr;
   }

   @Nullable
   public static <T> T fromBytes(RegistryAccess access, byte[] arr, Class<T> cls, @Nullable T ans, Predicate<SerialField> pred) {
      RegistryFriendlyByteBuf buf = decode(access, arr);
      return Wrappers.get(() -> (T)UnifiedCodec.deserializeValue(new PacketContext(buf, pred), buf, TypeInfo.of(cls), ans));
   }

   public static <T extends R, R> byte[] toBytes(RegistryAccess access, T obj, Class<R> r, Predicate<SerialField> pred) {
      RegistryFriendlyByteBuf buf = unit(access);
      Wrappers.run(() -> UnifiedCodec.serializeValue(new PacketContext(buf, pred), TypeInfo.of(r), obj));
      return encode(buf);
   }

   @Nullable
   public static <T> T from(RegistryFriendlyByteBuf buf, Class<T> cls, @Nullable T ans) {
      return Wrappers.get(() -> (T)UnifiedCodec.deserializeValue(new PacketContext(buf), buf, TypeInfo.of(cls), ans));
   }

   @Nullable
   public static <T> T from(RegistryFriendlyByteBuf buf, Class<T> cls, @Nullable T ans, Predicate<SerialField> pred) {
      return Wrappers.get(() -> (T)UnifiedCodec.deserializeValue(new PacketContext(buf, pred), buf, TypeInfo.of(cls), ans));
   }

   public static <T> void to(RegistryFriendlyByteBuf buf, T obj) {
      to(buf, obj, Wrappers.cast(obj.getClass()));
   }

   public static <T extends R, R> void to(RegistryFriendlyByteBuf buf, T obj, Class<R> r) {
      if (buf.registryAccess() == null) {
         throw new IllegalStateException("RegistryAccess cannot be null");
      }

      Wrappers.run(() -> UnifiedCodec.serializeValue(new PacketContext(buf), TypeInfo.of(r), obj));
   }

   public static <T extends R, R> void to(RegistryFriendlyByteBuf buf, T obj, Class<R> r, Predicate<SerialField> pred) {
      Wrappers.run(() -> UnifiedCodec.serializeValue(new PacketContext(buf, pred), TypeInfo.of(r), obj));
   }
}

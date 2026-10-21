package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class ClassHandler<R extends Tag, T> implements JsonClassHandler<T>, NBTClassHandler<R, T>, PacketClassHandler<T> {
   public final Function<Object, JsonElement> toJson;
   public final Function<JsonElement, ?> fromJson;
   public final Function<RegistryFriendlyByteBuf, ?> fromPacket;
   public final BiConsumer<RegistryFriendlyByteBuf, Object> toPacket;
   public final Function<Tag, ?> fromTag;
   public final Function<Object, Tag> toTag;

   @ParametersAreNullableByDefault
   public ClassHandler(
      @Nonnull Class<T> cls,
      Function<T, JsonElement> tj,
      Function<JsonElement, T> fj,
      Function<RegistryFriendlyByteBuf, T> fp,
      BiConsumer<RegistryFriendlyByteBuf, T> tp,
      Function<R, T> ft,
      Function<T, Tag> tt,
      @Nonnull Class<?>... others
   ) {
      this.toJson = tj;
      this.fromJson = fj;
      this.fromPacket = fp;
      this.toPacket = tp;
      this.fromTag = ft;
      this.toTag = tt;
      this.put(cls);

      for (Class<?> c : others) {
         this.put(c);
      }
   }

   private void put(Class<?> cls) {
      if (this.toJson != null && this.fromJson != null) {
         Handlers.JSON_MAP.putIfAbsent(cls, this);
      }

      if (this.fromTag != null && this.toTag != null) {
         Handlers.NBT_MAP.putIfAbsent(cls, this);
      }

      if (this.fromPacket != null && this.toPacket != null) {
         Handlers.PACKET_MAP.putIfAbsent(cls, this);
      }
   }

   @Override
   public T fromTag(Tag tag) {
      return (T)this.fromTag.apply(tag);
   }

   @Override
   public R toTag(Object obj) {
      return (R)this.toTag.apply(obj);
   }

   @Override
   public JsonElement toJson(Object obj) {
      return this.toJson.apply(obj);
   }

   @Override
   public T fromJson(JsonElement e) {
      return (T)this.fromJson.apply(e);
   }

   @Override
   public void toPacket(RegistryFriendlyByteBuf buf, Object obj) {
      this.toPacket.accept(buf, obj);
   }

   @Override
   public T fromPacket(RegistryFriendlyByteBuf buf) {
      return (T)this.fromPacket.apply(buf);
   }
}

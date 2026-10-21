package dev.arakiel.gemini.component.internal.network;

import dev.arakiel.gemini.component.api.ComponentKey;
import dev.arakiel.gemini.component.api.ComponentRegistry;
import dev.arakiel.gemini.component.internal.base.UnknownComponentException;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record ComponentUpdatePayload<T>(
   Type<ComponentUpdatePayload<T>> id, T targetData, boolean required, ResourceLocation componentKeyId, RegistryFriendlyByteBuf buf
) implements CustomPacketPayload {
   public static <T> Type<ComponentUpdatePayload<T>> id(String path) {
      return new Type<>(ResourceLocation.fromNamespaceAndPath("gemini", path));
   }

   public static <T> void register(Type<ComponentUpdatePayload<T>> id, StreamCodec<? super RegistryFriendlyByteBuf, T> targetDataCodec) {
      PayloadTypeRegistry.playS2C().register(id, codec(id, targetDataCodec));
   }

   public static <T> StreamCodec<RegistryFriendlyByteBuf, ComponentUpdatePayload<T>> codec(
      Type<ComponentUpdatePayload<T>> id, StreamCodec<? super RegistryFriendlyByteBuf, T> targetDataCodec
   ) {
      return StreamCodec.composite(
         StreamCodec.unit(id),
         ComponentUpdatePayload::id,
         targetDataCodec,
         ComponentUpdatePayload::targetData,
         ByteBufCodecs.BOOL,
         ComponentUpdatePayload::required,
         ResourceLocation.STREAM_CODEC,
         ComponentUpdatePayload::componentKeyId,
         MorePacketCodecs.REG_BYTE_BUF,
         ComponentUpdatePayload::buf,
         ComponentUpdatePayload::new
      );
   }

   public Optional<ComponentKey<?>> componentKey() {
      ComponentKey<?> key = ComponentRegistry.get(this.componentKeyId());
      if (key == null && this.required()) {
         throw new UnknownComponentException("Unknown component " + this.componentKeyId());
      } else {
         return Optional.ofNullable(key);
      }
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return this.id;
   }
}

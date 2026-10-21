package dev.xkmc.l2core.init.reg.simple;

import dev.xkmc.l2core.capability.attachment.AttachmentDef;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public record AttReg(DeferredRegister<AttachmentType<?>> att) {
   public static AttReg of(Reg reg) {
      return new AttReg(reg.make(NeoForgeRegistries.ATTACHMENT_TYPES));
   }

   public <E, T extends AttachmentDef<E>> AttVal<E, T> reg(String id, T type) {
      return new AttReg.AttValImpl<>(this.att.register(id, type::type), type);
   }

   public <E, T extends AttachmentDef<E>> AttVal<E, T> reg(String id, Function<ResourceLocation, T> factory) {
      ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(this.att.getNamespace(), id);
      T type = (T)factory.apply(rl);
      return this.reg(id, type);
   }

   public <E extends GeneralCapabilityTemplate<H, E>, H extends IAttachmentHolder> AttVal.CapVal<H, E> entity(
      String id, Class<E> holder_class, Supplier<E> sup, Class<H> entity_class, Predicate<H> pred
   ) {
      ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(this.att.getNamespace(), id);
      GeneralCapabilityHolder<H, E> type = new GeneralCapabilityHolder<>(rl, holder_class, sup, entity_class, pred);
      return new AttReg.CapValImpl<>(this.att.register(id, type::type), type);
   }

   public <E extends PlayerCapabilityTemplate<E>> AttVal.PlayerVal<E> player(
      String id, Class<E> holder_class, Supplier<E> sup, PlayerCapabilityHolder.NetworkFactory<E> network
   ) {
      ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(this.att.getNamespace(), id);
      PlayerCapabilityHolder<E> type = new PlayerCapabilityHolder<>(rl, holder_class, sup, network);
      return new AttReg.PlayerValImpl<>(this.att.register(id, type::type), type);
   }

   private record AttValImpl<E, T extends AttachmentDef<E>>(DeferredHolder<AttachmentType<?>, AttachmentType<E>> val, T type) implements AttVal<E, T> {
      public AttachmentType<E> get() {
         return (AttachmentType<E>)this.val.get();
      }

      @Override
      public ResourceLocation id() {
         return this.val.getId();
      }
   }

   private record CapValImpl<E extends IAttachmentHolder, T extends GeneralCapabilityTemplate<E, T>>(
      DeferredHolder<AttachmentType<?>, AttachmentType<T>> val, GeneralCapabilityHolder<E, T> type
   ) implements AttVal.CapVal<E, T> {
      public AttachmentType<T> get() {
         return (AttachmentType<T>)this.val.get();
      }

      @Override
      public ResourceLocation id() {
         return this.val.getId();
      }
   }

   private record PlayerValImpl<T extends PlayerCapabilityTemplate<T>>(DeferredHolder<AttachmentType<?>, AttachmentType<T>> val, PlayerCapabilityHolder<T> type)
      implements AttVal.PlayerVal<T> {
      public AttachmentType<T> get() {
         return (AttachmentType<T>)this.val.get();
      }

      @Override
      public ResourceLocation id() {
         return this.val.getId();
      }
   }
}

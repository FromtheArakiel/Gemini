package dev.xkmc.l2core.init.reg.simple;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public record SR<T>(DeferredRegister<T> reg) {
   public static <T> SR<T> of(Reg parent, Registry<T> reg) {
      return new SR<>(parent.make(reg));
   }

   public static <T> SR<T> of(Reg parent, ResourceKey<Registry<T>> reg) {
      return new SR<>(parent.make(reg));
   }

   public <H extends T> SR.ValImpl<T, H> reg(String id, Supplier<H> sup) {
      return new SR.ValImpl<>(this.reg.register(id, sup));
   }

   public <H extends T> SR.ValImpl<T, H> reg(String id, Function<ResourceLocation, H> sup) {
      return new SR.ValImpl<>(this.reg.register(id, sup));
   }

   public record ValImpl<R, T extends R>(DeferredHolder<R, T> val) implements Val<T> {
      @Override
      public T get() {
         return (T)this.val.get();
      }

      @Override
      public ResourceLocation id() {
         return this.val.getId();
      }
   }
}

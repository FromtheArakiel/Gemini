package dev.xkmc.l2core.init.reg.simple;

import com.tterrag.registrate.util.entry.RegistryEntry;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public interface Val<T> extends Supplier<T> {
   @Override
   T get();

   ResourceLocation id();

   DeferredHolder<? super T, T> val();

   default ResourceKey<? super T> key() {
      return this.val().getKey();
   }

   record Registrate<H, T extends H>(RegistryEntry<H, T> val) implements Val<T> {
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

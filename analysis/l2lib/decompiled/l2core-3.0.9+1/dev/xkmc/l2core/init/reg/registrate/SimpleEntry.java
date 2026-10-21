package dev.xkmc.l2core.init.reg.registrate;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Kind;
import net.minecraft.resources.ResourceKey;

public record SimpleEntry<T>(RegistryEntry<T, ? extends T> val) implements LegacyHolder<T> {
   @Override
   public ResourceKey<T> key() {
      return this.val.getKey();
   }

   @Override
   public T get() {
      return (T)this.val.get();
   }

   @Override
   public Holder<T> holder() {
      return this.val;
   }

   @Override
   public int hashCode() {
      return this.key().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj ? true : obj instanceof Holder<?> h && h.kind() == Kind.REFERENCE && this.key().equals(h.getKey());
   }
}

package com.tterrag.registrate.util.entry;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RegistryEntry<R, S extends R> extends DeferredHolder<R, S> implements NonNullSupplier<S> {
   private final AbstractRegistrate<?> owner;

   public RegistryEntry(AbstractRegistrate<?> owner, DeferredHolder<R, S> key) {
      super(key.getKey());
      if (owner == null) {
         throw new NullPointerException("Owner must not be null");
      }

      this.owner = owner;
   }

   public <X, Y extends X> RegistryEntry<X, Y> getSibling(ResourceKey<? extends Registry<X>> registryType) {
      return (RegistryEntry<X, Y>)this.owner.get(this.getId().getPath(), (ResourceKey<? extends Registry<R>>)registryType);
   }

   public <X, Y extends X> RegistryEntry<X, Y> getSibling(Registry<X> registry) {
      return this.getSibling(registry.key());
   }

   public Optional<RegistryEntry<R, S>> filter(Predicate<R> predicate) {
      Objects.requireNonNull(predicate);
      return predicate.test((R)this.get()) ? Optional.of(this) : Optional.empty();
   }

   public <X> boolean is(X entry) {
      return this.get() == entry;
   }

   protected static <E extends RegistryEntry<?, ?>> E cast(Class<? super E> clazz, RegistryEntry<?, ?> entry) {
      if (clazz.isInstance(entry)) {
         return (E)entry;
      } else {
         throw new IllegalArgumentException("Could not convert RegistryEntry: expecting " + clazz + ", found " + entry.getClass());
      }
   }
}

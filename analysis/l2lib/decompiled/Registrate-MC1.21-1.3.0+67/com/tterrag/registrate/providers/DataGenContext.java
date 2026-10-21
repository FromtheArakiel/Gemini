package com.tterrag.registrate.providers;

import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class DataGenContext<R, E extends R> implements NonNullSupplier<E> {
   private final NonNullSupplier<E> entry;
   private final String name;
   private final ResourceLocation id;

   @NonnullType
   public E getEntry() {
      return this.entry.get();
   }

   @Deprecated
   public static <R, E extends R> DataGenContext<R, E> from(Builder<R, E, ?, ?> builder, ResourceKey<? extends Registry<R>> type) {
      return from(builder);
   }

   public static <R, E extends R> DataGenContext<R, E> from(Builder<R, E, ?, ?> builder) {
      return new DataGenContext<>(
         NonNullSupplier.of(builder.getOwner().get(builder.getName(), builder.getRegistryKey())),
         builder.getName(),
         ResourceLocation.fromNamespaceAndPath(builder.getOwner().getModid(), builder.getName())
      );
   }

   public DataGenContext(NonNullSupplier<E> entry, String name, ResourceLocation id) {
      this.entry = entry;
      this.name = name;
      this.id = id;
   }

   public String getName() {
      return this.name;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DataGenContext<?, ?> other)) {
         return false;
      } else {
         Object this$entry = this.getEntry();
         Object other$entry = other.getEntry();
         if (this$entry == null ? other$entry == null : this$entry.equals(other$entry)) {
            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null ? other$name == null : this$name.equals(other$name)) {
               Object this$id = this.getId();
               Object other$id = other.getId();
               return this$id == null ? other$id == null : this$id.equals(other$id);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $entry = this.getEntry();
      result = result * 59 + ($entry == null ? 43 : $entry.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $id = this.getId();
      return result * 59 + ($id == null ? 43 : $id.hashCode());
   }

   @Override
   public String toString() {
      return "DataGenContext(entry=" + this.getEntry() + ", name=" + this.getName() + ", id=" + this.getId() + ")";
   }

   @Override
   public E get() {
      return this.entry.get();
   }

   @Override
   public NonNullSupplier<E> lazy() {
      return this.entry.lazy();
   }
}

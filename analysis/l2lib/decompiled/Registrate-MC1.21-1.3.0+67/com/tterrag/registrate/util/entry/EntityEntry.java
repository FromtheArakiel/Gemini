package com.tterrag.registrate.util.entry;

import com.tterrag.registrate.AbstractRegistrate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EntityEntry<T extends Entity> extends RegistryEntry<EntityType<?>, EntityType<T>> {
   public EntityEntry(AbstractRegistrate<?> owner, DeferredHolder<EntityType<?>, EntityType<T>> delegate) {
      super(owner, delegate);
   }

   @Nullable
   public T create(Level world) {
      return (T)this.get().create(world);
   }

   public boolean is(Entity t) {
      return t != null && t.getType() == this.get();
   }

   public static <T extends Entity> EntityEntry<T> cast(RegistryEntry<EntityType<?>, EntityType<T>> entry) {
      return RegistryEntry.cast(EntityEntry.class, entry);
   }
}

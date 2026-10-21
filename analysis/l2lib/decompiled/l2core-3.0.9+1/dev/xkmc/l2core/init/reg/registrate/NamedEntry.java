package dev.xkmc.l2core.init.reg.registrate;

import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class NamedEntry<T extends NamedEntry<T>> {
   private final L2Registrate.RegistryInstance<T> registry;
   private String desc = null;
   private ResourceLocation id = null;

   public NamedEntry(L2Registrate.RegistryInstance<T> registry) {
      this.registry = registry;
   }

   public String getDescriptionId() {
      if (this.desc != null) {
         return this.desc;
      }

      ResourceLocation rl = this.getRegistryName();
      ResourceLocation reg = this.registry.key().location();
      this.desc = reg.getPath() + "." + rl.getNamespace() + "." + rl.getPath();
      return this.desc;
   }

   public MutableComponent getDesc() {
      return Component.translatable(this.getDescriptionId());
   }

   public ResourceLocation getRegistryName() {
      if (this.id != null) {
         return this.id;
      } else {
         this.id = this.registry.get().getKey(this.getThis());
         if (this.id == null) {
            throw new IllegalStateException("Entry %s is not registered".formatted(this.getClass().getSimpleName()));
         } else {
            return this.id;
         }
      }
   }

   public String getID() {
      return this.getRegistryName().toString();
   }

   public T getThis() {
      return (T)Wrappers.cast(this);
   }

   public Holder<T> holder() {
      return this.registry.get().wrapAsHolder(this.getThis());
   }
}

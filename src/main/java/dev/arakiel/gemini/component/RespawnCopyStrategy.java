package dev.arakiel.gemini.component;

import dev.arakiel.gemini.component.internal.entity.GeminiEntityInternals;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface RespawnCopyStrategy<C extends Component> {
   RespawnCopyStrategy<Component> ALWAYS_COPY = (from, to, registryLookup, lossless, keepInventory, sameCharacter) -> copy(from, to, registryLookup);
   RespawnCopyStrategy<Component> CHARACTER = (from, to, registryLookup, lossless, keepInventory, sameCharacter) -> {
      if (sameCharacter) {
         copy(from, to, registryLookup);
      }
   };
   RespawnCopyStrategy<Component> INVENTORY = (from, to, registryLookup, lossless, keepInventory, sameCharacter) -> {
      if (lossless || keepInventory) {
         copy(from, to, registryLookup);
      }
   };
   RespawnCopyStrategy<Component> LOSSLESS_ONLY = (from, to, registryLookup, lossless, keepInventory, sameCharacter) -> {
      if (lossless) {
         copy(from, to, registryLookup);
      }
   };
   RespawnCopyStrategy<Component> NEVER_COPY = (from, to, registryLookup, lossless, keepInventory, sameCharacter) -> {
   };
   ResourceLocation EVENT_PHASE = ResourceLocation.fromNamespaceAndPath("gemini", "component-copy");

   static <C extends Component> RespawnCopyStrategy<? super C> get(ComponentKey<C> key, Class<? extends LivingEntity> entityClass) {
      return GeminiEntityInternals.getRespawnCopyStrategy(key, entityClass);
   }

   static <C extends Component> void copy(C from, C to, Provider registryLookup) {
      if (to instanceof CopyableComponent<?> copyable) {
         GeminiEntityInternals.copyAsCopyable(from, copyable, registryLookup);
      } else {
         CompoundTag tag = new CompoundTag();
         from.writeToNbt(tag, registryLookup);
         to.readFromNbt(tag, registryLookup);
      }
   }

   void copyForRespawn(C var1, C var2, Provider var3, boolean var4, boolean var5, boolean var6);
}
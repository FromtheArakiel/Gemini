package dev.arakiel.gemini.mixin.component.base;

import dev.arakiel.gemini.component.Component;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Component.class})
public interface ComponentMixin {
   @Unique
   void gemini$readFromNbt(CompoundTag var1);

   @Unique
   void gemini$writeToNbt(CompoundTag var1);

   @Overwrite
   default void readFromNbt(CompoundTag tag, Provider registryLookup) {
      this.gemini$readFromNbt(tag);
   }

   @Overwrite
   default void writeToNbt(CompoundTag tag, Provider registryLookup) {
      this.gemini$writeToNbt(tag);
   }
}

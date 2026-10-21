package dev.arakiel.gemini.component.api;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Contract;

public interface Component {
   @Contract(
      mutates = "this"
   )
   void readFromNbt(CompoundTag var1, Provider var2);

   @Contract(
      mutates = "param1"
   )
   void writeToNbt(CompoundTag var1, Provider var2);
}

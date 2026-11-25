package dev.arakiel.gemini.component;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface NbtSerializable {
   @Contract(
      mutates = "this"
   )
   void fromTag(CompoundTag var1, Provider var2);

   @Contract(
      mutates = "param"
   )
   CompoundTag toTag(CompoundTag var1, Provider var2);

   @Contract(
      mutates = "this"
   )
   default void fromDynamic(Dynamic<?> dynamic, Provider registryLookup) {
      this.fromTag((CompoundTag)dynamic.convert(NbtOps.INSTANCE).getValue(), registryLookup);
   }

   @Contract(
      pure = true
   )
   default <T> Dynamic<T> toDynamic(Dynamic<T> dynamic, Provider wrapperLookup) {
      return dynamic.convert(NbtOps.INSTANCE).map(tag -> this.toTag((CompoundTag)tag, wrapperLookup)).convert(dynamic.getOps());
   }
}

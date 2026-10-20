package dev.arakiel.gemini.component.entity;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.CopyableComponent;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface RespawnableComponent<C extends Component> extends Component, CopyableComponent<C> {
   @Contract(
      pure = true
   )
   default boolean shouldCopyForRespawn(boolean lossless, boolean keepInventory, boolean sameCharacter) {
      return lossless;
   }

   default void copyForRespawn(C original, Provider registryLookup, boolean lossless, boolean keepInventory, boolean sameCharacter) {
      this.copyFrom(original, registryLookup);
   }

   @Override
   default void copyFrom(C other, Provider registryLookup) {
      CompoundTag tag = new CompoundTag();
      other.writeToNbt(tag, registryLookup);
      this.readFromNbt(tag, registryLookup);
   }
}

package dev.arakiel.gemini.component.internal.world;

import dev.arakiel.gemini.component.ComponentContainer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;

public class ComponentPersistentState extends SavedData {
   private final ComponentContainer components;

   public static Factory<ComponentPersistentState> getType(ComponentContainer components) {
      return new Factory<>(
         () -> new ComponentPersistentState(components), (tag, registryLookup) -> fromNbt(components, tag, registryLookup), DataFixTypes.LEVEL
      );
   }

   public ComponentPersistentState(ComponentContainer components) {
      this.components = components;
   }

   @Override
   public boolean isDirty() {
      return true;
   }

   @Override
   public CompoundTag save(CompoundTag tag, Provider registryLookup) {
      return this.components.toTag(tag, registryLookup);
   }

   public static ComponentPersistentState fromNbt(ComponentContainer components, CompoundTag tag, Provider registryLookup) {
      ComponentPersistentState state = new ComponentPersistentState(components);
      state.components.fromTag(tag, registryLookup);
      return state;
   }
}

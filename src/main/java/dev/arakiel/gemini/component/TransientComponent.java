package dev.arakiel.gemini.component;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface TransientComponent extends Component {
   @Override
   default void readFromNbt(CompoundTag tag, Provider registryLookup) {
   }

   @Override
   default void writeToNbt(CompoundTag tag, Provider registryLookup) {
   }

   public abstract static class SimpleImpl implements TransientComponent {
      @Override
      public int hashCode() {
         return 1337;
      }

      @Override
      public boolean equals(@Nullable Object obj) {
         return obj != null && obj.getClass() == this.getClass();
      }
   }
}

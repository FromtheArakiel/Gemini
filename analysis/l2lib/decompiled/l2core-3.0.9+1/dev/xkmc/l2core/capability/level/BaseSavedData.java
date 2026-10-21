package dev.xkmc.l2core.capability.level;

import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

@SerialClass
public class BaseSavedData<T> extends SavedData {
   private final Class<T> cls;

   public BaseSavedData(Class<T> cls) {
      this.cls = cls;
   }

   public CompoundTag save(CompoundTag tag, Provider provider) {
      new TagCodec(provider).toTag(tag, this.cls, this);
      return tag;
   }

   public boolean isDirty() {
      return true;
   }
}

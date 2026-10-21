package dev.xkmc.l2serial.serialization.codec;

import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.serialization.type_cache.ClassCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.serialization.unified_processor.TagContext;
import dev.xkmc.l2serial.serialization.unified_processor.UnifiedCodec;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class TagCodec {
   private final Provider access;
   private Predicate<SerialField> pred = e -> true;

   public TagCodec(Provider access) {
      this.access = access;
   }

   public TagCodec pred(Predicate<SerialField> pred) {
      this.pred = pred;
      return this;
   }

   @Nullable
   public <T> T fromTag(CompoundTag tag, Class<?> cls) {
      return this.fromTag(tag, cls, null);
   }

   @Nullable
   public <T> T fromTag(CompoundTag tag, Class<?> cls, @Nullable T obj) {
      return Wrappers.get(() -> (T)UnifiedCodec.deserializeObject(new TagContext(this.access, this.pred), tag, ClassCache.get(cls), obj));
   }

   @Nullable
   public CompoundTag toTag(CompoundTag tag, Object obj) {
      return this.toTag(tag, obj.getClass(), obj);
   }

   @Nullable
   public CompoundTag toTag(CompoundTag tag, Class<?> cls, Object obj) {
      return Wrappers.get(() -> UnifiedCodec.serializeObject(new TagContext(this.access, this.pred), tag, ClassCache.get(cls), obj));
   }

   @Nullable
   public <T> T valueFromTag(Tag tag, Class<?> cls) {
      return Wrappers.get(() -> (T)UnifiedCodec.deserializeValue(new TagContext(this.access, this.pred), tag, TypeInfo.of(cls), null));
   }

   @Nullable
   public Tag valueToTag(Class<?> cls, Object obj) {
      return Wrappers.get(() -> UnifiedCodec.serializeValue(new TagContext(this.access, this.pred), TypeInfo.of(cls), obj));
   }
}

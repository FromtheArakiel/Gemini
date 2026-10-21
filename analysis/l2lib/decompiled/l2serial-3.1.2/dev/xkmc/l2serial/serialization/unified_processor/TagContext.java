package dev.xkmc.l2serial.serialization.unified_processor;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import dev.xkmc.l2serial.serialization.generic_types.CodecReg;
import dev.xkmc.l2serial.serialization.generic_types.HolderCodec;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.serialization.type_cache.FieldCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class TagContext extends TreeContext<Tag, CompoundTag, ListTag> {
   private static final CompoundTag NULL = new CompoundTag();
   private final Predicate<SerialField> pred;

   public TagContext(DynamicOps<Tag> ops, Predicate<SerialField> pred) {
      super(Optional.of(Pair.of(NULL, Optional.empty())), ops);
      this.pred = pred;
   }

   public TagContext(Provider access, Predicate<SerialField> pred) {
      this(access.createSerializationContext(NbtOps.INSTANCE), pred);
   }

   public Optional<Either<Optional<Object>, TypeInfo>> fetchRealClass(@Nullable Tag e, TypeInfo def) throws Exception {
      if (e != null && !(e instanceof CompoundTag tag && tag.contains("_null"))) {
         if (e instanceof CompoundTag obj && obj.contains("_class")) {
            Tag tcls = obj.get("_class");
            if (tcls != null) {
               String scls = tcls.getAsString();
               if (!scls.isEmpty()) {
                  return Optional.of(Either.right(TypeInfo.of(Class.forName(scls))));
               }
            }
         }

         return Optional.empty();
      } else {
         return Optional.of(Either.left(Optional.empty()));
      }
   }

   public Object deserializeEfficientMap(Tag tag, TypeInfo key, TypeInfo val, Object def) throws Exception {
      CompoundTag ctag = (CompoundTag)tag;
      Map map = (Map)def;
      map.clear();

      for (String str : ctag.getAllKeys()) {
         Class kcls = key.getAsClass();
         Object mkey = null;
         if (kcls == String.class) {
            mkey = str;
         } else if (kcls.isEnum()) {
            mkey = Enum.valueOf(kcls, str);
         } else if (HolderCodec.INS.predicate(kcls)) {
            mkey = HolderCodec.INS.deserializeValue(this, StringTag.valueOf(str), key, null);
         } else if (Handlers.CODEC_MAP.containsKey(kcls)) {
            mkey = this.deserializeCodec(Handlers.CODEC_MAP.get(kcls), StringTag.valueOf(str));
         } else if (Handlers.NBT_MAP.containsKey(kcls)) {
            mkey = Handlers.NBT_MAP.get(kcls).fromTag(StringTag.valueOf(str));
         }

         Tag t = ctag.get(str);
         if (mkey != null) {
            map.put(mkey, UnifiedCodec.deserializeValue(this, t == null ? NULL : t, val, null));
         }
      }

      return map;
   }

   @Override
   public boolean hasSpecialHandling(Class<?> cls) {
      return Handlers.NBT_MAP.containsKey(cls);
   }

   public Object deserializeSpecial(Class<?> cls, Tag e) {
      return Handlers.NBT_MAP.get(cls).fromTag(e);
   }

   public Tag serializeSpecial(Class<?> cls, Object obj) {
      return Handlers.NBT_MAP.get(cls).toTag(obj);
   }

   public Object deserializeCodec(CodecReg<?> cls, Tag e) {
      return ((Pair)cls.codec().decode(this.ops(), e).getOrThrow()).getFirst();
   }

   public Tag serializeCodec(CodecReg<?> cls, Object e) {
      return (Tag)cls.codec().encodeStart(this.ops(), Wrappers.cast(e)).getOrThrow();
   }

   public boolean shouldRead(CompoundTag obj, FieldCache field) throws Exception {
      return this.pred.test(field.getSerialAnnotation()) && obj.contains(field.getName());
   }

   @Override
   public boolean shouldReadMap(MapLike<Tag> obj, FieldCache field) throws Exception {
      return this.pred.test(field.getSerialAnnotation()) && obj.get(field.getName()) != null;
   }

   public Tag retrieve(CompoundTag obj, String field) {
      Tag t = obj.get(field);
      return (Tag)(t == null ? NULL : t);
   }

   public Tag retrieveMap(MapLike<Tag> obj, String field) {
      Tag t = (Tag)obj.get(field);
      return (Tag)(t == null ? NULL : t);
   }

   public ListTag castAsList(Tag e) {
      return (ListTag)e;
   }

   public int getSize(ListTag arr) {
      return arr.size();
   }

   public Tag getElement(ListTag arr, int i) {
      return arr.get(i);
   }

   public boolean isListFormat(Tag e) {
      return e instanceof ListTag;
   }

   public CompoundTag castAsMap(Tag e) {
      return (CompoundTag)e;
   }

   public String getAsString(Tag e) {
      return e == NULL ? "" : e.getAsString();
   }

   public void addField(CompoundTag obj, String str, @Nullable Tag e) {
      if (e != null) {
         obj.put(str, e);
      }
   }

   public ListTag createList(int size) {
      return new ListTag();
   }

   public CompoundTag createMap() {
      return new CompoundTag();
   }

   public void addListItem(ListTag arr, Tag e) {
      arr.add(e);
   }

   public boolean canBeString(Tag e) {
      return e instanceof StringTag;
   }

   public Tag fromString(String str) {
      return StringTag.valueOf(str);
   }

   @Override
   public boolean shouldWrite(SerialField sf) {
      return this.pred.test(sf);
   }

   static {
      NULL.putBoolean("_null", true);
   }
}

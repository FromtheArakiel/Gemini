package dev.xkmc.l2serial.serialization.unified_processor;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapLike;
import dev.xkmc.l2serial.serialization.generic_types.CodecReg;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.serialization.type_cache.ClassCache;
import dev.xkmc.l2serial.serialization.type_cache.FieldCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.Optional;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.Nullable;

public class RealCodecContext<T> extends TreeContext<T, T, T> {
   private final RegistryOps<T> ops;

   protected RealCodecContext(Optional<Pair<T, Optional<ClassCache>>> nil, RegistryOps<T> ops) {
      super(nil, ops);
      this.ops = ops;
   }

   @Override
   public boolean hasSpecialHandling(Class<?> cls) {
      return false;
   }

   @Override
   public Object deserializeSpecial(Class<?> cls, T t) {
      return null;
   }

   @Override
   public T serializeSpecial(Class<?> cls, Object obj) {
      return null;
   }

   @Override
   public Object deserializeCodec(CodecReg<?> cls, T t) {
      return ((Pair)cls.codec().decode(this.ops, t).getOrThrow()).getFirst();
   }

   @Override
   public T serializeCodec(CodecReg<?> cls, Object e) {
      return (T)cls.codec().encodeStart(this.ops, Wrappers.cast(e)).getOrThrow();
   }

   @Override
   public Optional<Either<Optional<Object>, TypeInfo>> fetchRealClass(@Nullable T e, TypeInfo def) throws Exception {
      if (e == null) {
         return Optional.of(Either.left(Optional.empty()));
      }

      DataResult<MapLike<T>> map = this.ops.getMap(e);
      if (map.isSuccess()) {
         MapLike<T> m = (MapLike<T>)map.getOrThrow();
         T scls = (T)m.get("_class");
         if (scls != null) {
            DataResult<String> cls = this.ops.getStringValue(scls);
            if (cls.isSuccess() && !((String)cls.getOrThrow()).isEmpty()) {
               return Optional.of(Either.right(TypeInfo.of(Class.forName((String)cls.getOrThrow()))));
            }
         }
      }

      return Optional.empty();
   }

   @Override
   public boolean shouldRead(T obj, FieldCache field) throws Exception {
      return this.ops.get(obj, field.getName()).isSuccess();
   }

   @Override
   public T retrieve(T obj, String field) {
      return (T)this.ops.get(obj, field).getOrThrow();
   }

   @Override
   public T castAsList(T t) {
      return t;
   }

   @Override
   public int getSize(T t) {
      return 0;
   }

   @Override
   public T getElement(T arr, int i) {
      return arr;
   }

   @Override
   public boolean isListFormat(T t) {
      return this.ops.getList(t).isSuccess();
   }

   @Override
   public T castAsMap(T element) {
      return element;
   }

   @Override
   public Object deserializeEfficientMap(T t, TypeInfo ckey, TypeInfo cval, Object ans) throws Exception {
      return null;
   }

   @Override
   public String getAsString(T t) {
      return (String)this.ops.getStringValue(t).getOrThrow();
   }

   @Override
   public void addField(T obj, String str, T t) {
      this.ops.set(obj, str, t);
   }

   @Override
   public T createList(int size) {
      return (T)this.ops.emptyList();
   }

   @Override
   public T createMap() {
      return (T)this.ops.emptyMap();
   }

   @Override
   public void addListItem(T t, T t2) {
      this.ops.mergeToList(t, t2);
   }

   @Override
   public boolean canBeString(T t) {
      return this.ops.getStringValue(t).isSuccess();
   }

   @Override
   public T fromString(String str) {
      return (T)this.ops.createString(str);
   }

   @Override
   public boolean shouldWrite(SerialField sf) {
      return true;
   }

   @Override
   public boolean shouldReadMap(MapLike<T> obj, FieldCache field) throws Exception {
      return obj.get(field.getName()) != null;
   }

   @Override
   public T retrieveMap(MapLike<T> obj, String field) {
      return (T)obj.get(field);
   }
}

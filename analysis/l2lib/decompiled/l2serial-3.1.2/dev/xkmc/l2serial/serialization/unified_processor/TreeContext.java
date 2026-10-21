package dev.xkmc.l2serial.serialization.unified_processor;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import dev.xkmc.l2serial.serialization.type_cache.ClassCache;
import dev.xkmc.l2serial.serialization.type_cache.FieldCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.Optional;
import javax.annotation.Nullable;

public abstract class TreeContext<E, O extends E, A extends E> extends UnifiedContext<E, O, A> {
   private final Optional<Pair<E, Optional<ClassCache>>> nil;
   private final DynamicOps<E> ops;

   protected TreeContext(Optional<Pair<E, Optional<ClassCache>>> nil, DynamicOps<E> ops) {
      this.nil = nil;
      this.ops = ops;
   }

   protected DynamicOps<E> ops() {
      return this.ops;
   }

   @Override
   public E getKeyOfEntry(O obj) {
      return this.retrieve(obj, "_key");
   }

   @Override
   public E getValueOfEntry(O obj) {
      return this.retrieve(obj, "_val");
   }

   @Override
   public void setKeyOfEntry(O obj, E e) {
      this.addField(obj, "_key", e);
   }

   @Override
   public void setValueOfEntry(O obj, E e) {
      this.addField(obj, "_val", e);
   }

   public abstract boolean shouldReadMap(MapLike<E> var1, FieldCache var2) throws Exception;

   public abstract E retrieveMap(MapLike<E> var1, String var2);

   @Override
   public Optional<Pair<E, Optional<ClassCache>>> writeRealClass(TypeInfo cls, @Nullable Object obj) throws Exception {
      if (obj == null) {
         return this.nil;
      }

      Optional<Wrappers.ExcSup<E>> special = UnifiedCodec.serializeSpecial(this, cls, obj);
      if (special.isPresent()) {
         return Optional.of(Pair.of(special.get().get(), Optional.empty()));
      }

      if (obj.getClass() != cls.getAsClass()) {
         ClassCache cache = ClassCache.get(obj.getClass());
         if (cache.getSerialAnnotation() != null) {
            O ans = this.createMap();
            this.addField(ans, "_class", this.fromString(obj.getClass().getName()));
            return Optional.of(Pair.of(ans, Optional.of(cache)));
         }
      }

      return Optional.empty();
   }

   @Override
   public void addOptionalClass(O obj, Class<?> objcls, Class<?> clsinfo) {
      if (objcls != clsinfo) {
         this.addField(obj, "_class", this.fromString(objcls.getName()));
      }
   }
}

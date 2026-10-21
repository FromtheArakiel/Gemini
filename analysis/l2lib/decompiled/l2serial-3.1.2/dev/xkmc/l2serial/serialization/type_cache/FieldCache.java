package dev.xkmc.l2serial.serialization.type_cache;

import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.util.Wrappers;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class FieldCache {
   private final Field field;
   private final String name;
   private final Map<Class<? extends Annotation>, ? extends Annotation> cache = new HashMap<>();

   FieldCache(Field field) {
      this.field = field;
      this.name = field.getName();
      this.field.setAccessible(true);
   }

   @Nullable
   public <T extends Annotation> T getAnnotation(Class<T> cls) throws Exception {
      if (this.cache.containsKey(cls)) {
         return Wrappers.cast(this.cache.get(cls));
      }

      T ans = this.field.getAnnotation(cls);
      this.cache.put(cls, Wrappers.cast(ans));
      return ans;
   }

   @Nullable
   public SerialField getSerialAnnotation() throws Exception {
      return this.getAnnotation(SerialField.class);
   }

   public String getName() {
      return this.name;
   }

   public Object get(@Nullable Object ans) throws Exception {
      return this.field.get(ans);
   }

   public void set(Object target, @Nullable Object value) throws Exception {
      this.field.set(target, value);
   }

   public TypeInfo toType() {
      return TypeInfo.of(this.field);
   }
}

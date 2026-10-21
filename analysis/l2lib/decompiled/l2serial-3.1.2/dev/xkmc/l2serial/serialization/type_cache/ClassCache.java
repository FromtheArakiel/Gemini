package dev.xkmc.l2serial.serialization.type_cache;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.util.LazyExc;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public class ClassCache {
   private static final Map<Class<?>, ClassCache> CACHE = new ConcurrentHashMap<>();
   public final Class<?> cls;
   private final LazyExc<SerialClass> annotation;
   private final LazyExc<ClassCache> superClass;
   private final LazyExc<Constructor<?>> constructor;
   private final LazyExc<FieldCache[]> fields;
   private final LazyExc<MethodCache[]> methods;

   public static ClassCache get(Class<?> cls) {
      return CACHE.computeIfAbsent(cls, ClassCache::new);
   }

   private ClassCache(Class<?> cls) {
      this.cls = cls;
      this.constructor = new LazyExc<>(() -> {
         Constructor<?> ans = cls.getConstructor();
         ans.setAccessible(true);
         return ans;
      });
      this.annotation = new LazyExc<>(() -> cls.getAnnotation(SerialClass.class));
      this.fields = new LazyExc<>(() -> Arrays.stream(cls.getDeclaredFields()).map(FieldCache::new).toArray(FieldCache[]::new));
      this.methods = new LazyExc<>(() -> Arrays.stream(cls.getDeclaredMethods()).map(MethodCache::new).toArray(MethodCache[]::new));
      this.superClass = new LazyExc<>(() -> get(cls.getSuperclass()));
   }

   public Object create() throws Exception {
      return this.constructor.get().newInstance();
   }

   @Nullable
   public SerialClass getSerialAnnotation() throws Exception {
      return this.annotation.get();
   }

   public FieldCache[] getFields() throws Exception {
      return this.fields.get();
   }

   public ClassCache getSuperclass() throws Exception {
      return this.superClass.get();
   }

   public MethodCache[] getMethods() throws Exception {
      return this.methods.get();
   }
}

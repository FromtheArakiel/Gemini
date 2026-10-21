package dev.xkmc.l2serial.serialization.generic_types;

import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.serialization.unified_processor.UnifiedContext;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public abstract class GenericCodec {
   private static Set<Class<?>> NONMATCH = ConcurrentHashMap.newKeySet();
   private static Map<Class<?>, GenericCodec> CACHE = new ConcurrentHashMap<>();

   @Nullable
   public static GenericCodec find(TypeInfo info, @Nullable Object obj) {
      Class<?> cls = info.getAsClass();
      if (NONMATCH.contains(cls)) {
         return null;
      }

      GenericCodec e = CACHE.get(info.getAsClass());
      if (e != null) {
         return e;
      }

      for (GenericCodec x : Handlers.LIST) {
         if (x.predicate(cls)) {
            CACHE.put(cls, x);
            return x;
         }
      }

      NONMATCH.add(cls);
      return null;
   }

   protected GenericCodec() {
      synchronized (Handlers.LIST) {
         Handlers.LIST.add(this);
      }
   }

   public abstract boolean predicate(Class<?> var1);

   public abstract <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E> Object deserializeValue(
      C var1, E var2, TypeInfo var3, @Nullable Object var4
   ) throws Exception;

   public abstract <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E> E serializeValue(C var1, TypeInfo var2, Object var3) throws Exception;
}

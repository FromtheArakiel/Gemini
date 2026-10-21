package dev.xkmc.l2serial.serialization.type_cache;

import dev.xkmc.l2serial.serialization.marker.OnInject;
import dev.xkmc.l2serial.util.LazyExc;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

public class MethodCache {
   private final Method method;
   private final LazyExc<OnInject> serial;

   MethodCache(Method method) {
      this.method = method;
      this.serial = new LazyExc<>(() -> method.getAnnotation(OnInject.class));
   }

   @Nullable
   public OnInject getInjectAnnotation() throws Exception {
      return this.serial.get();
   }

   public void invoke(Object ans) throws Exception {
      this.method.invoke(ans);
   }
}

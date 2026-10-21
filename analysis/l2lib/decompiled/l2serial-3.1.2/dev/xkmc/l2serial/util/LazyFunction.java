package dev.xkmc.l2serial.util;

import java.util.function.Function;

public class LazyFunction<A, B> {
   private Function<A, B> func;
   private B value;

   public LazyFunction(Function<A, B> sup) {
      this.func = sup;
      this.value = null;
   }

   public static <A, B> LazyFunction<A, B> create(Function<A, B> sup) {
      return new LazyFunction<>(sup);
   }

   public B get(A a) {
      if (this.func != null) {
         this.value = this.func.apply(a);
         this.func = null;
      }

      return this.value;
   }
}

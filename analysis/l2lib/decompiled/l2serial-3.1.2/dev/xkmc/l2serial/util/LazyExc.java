package dev.xkmc.l2serial.util;

public class LazyExc<T> {
   private final Wrappers.ExcSup<T> factory;
   private T value;

   public LazyExc(Wrappers.ExcSup<T> factory) {
      this.factory = factory;
   }

   public T get() throws Exception {
      if (this.value != null) {
         return this.value;
      }

      synchronized (this) {
         this.value = this.factory.get();
      }

      return this.value;
   }
}

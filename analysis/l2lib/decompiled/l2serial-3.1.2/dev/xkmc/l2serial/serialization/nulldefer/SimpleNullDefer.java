package dev.xkmc.l2serial.serialization.nulldefer;

public class SimpleNullDefer<T> extends NullDefer<T> {
   private final T val;

   public SimpleNullDefer(Class<T> cls, T val) {
      super(cls);
      this.val = val;
   }

   @Override
   public boolean predicate(T obj) {
      return obj == this.val;
   }

   @Override
   public T getNullDefault() {
      return this.val;
   }
}

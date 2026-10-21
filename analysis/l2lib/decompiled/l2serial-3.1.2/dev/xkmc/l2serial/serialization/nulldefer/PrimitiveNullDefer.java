package dev.xkmc.l2serial.serialization.nulldefer;

public class PrimitiveNullDefer<T> extends SimpleNullDefer<T> {
   public PrimitiveNullDefer(Class<T> cls, T val) {
      super(cls, val);
   }

   @Override
   public boolean predicate(T obj) {
      return false;
   }
}

package dev.xkmc.l2core.capability.attachment;

import dev.xkmc.l2serial.util.Wrappers;

public class GeneralCapabilityTemplate<E, T extends GeneralCapabilityTemplate<E, T>> {
   public T getThis() {
      return (T)Wrappers.cast(this);
   }

   public void tick(E e) {
   }
}

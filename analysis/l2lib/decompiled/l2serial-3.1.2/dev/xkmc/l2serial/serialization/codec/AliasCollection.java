package dev.xkmc.l2serial.serialization.codec;

import java.util.List;
import javax.annotation.Nullable;

public interface AliasCollection<T> {
   List<T> getAsList();

   void clear();

   void set(int var1, int var2, @Nullable T var3);

   Class<T> getElemClass();

   default void setRaw(int n, int i, @Nullable Object elem) {
      this.set(n, i, (T)elem);
   }
}

package com.tterrag.registrate.util.nullness;

import java.util.Objects;
import java.util.function.Supplier;

@Deprecated
public interface NullableSupplier<T> extends Supplier<T> {
   @Override
   T get();

   default T getNonNull() {
      return this.getNonNull(() -> "Unexpected null value from supplier");
   }

   default T getNonNull(NonNullSupplier<String> errorMsg) {
      T res = this.get();
      Objects.requireNonNull(res, errorMsg);
      return res;
   }

   default NonNullSupplier<T> asNonNull() {
      return () -> this.getNonNull();
   }

   default NonNullSupplier<T> asNonNull(NonNullSupplier<String> errorMsg) {
      return () -> this.getNonNull(errorMsg);
   }
}

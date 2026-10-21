package com.tterrag.registrate.util.nullness;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface NonNullFunction<T, R> extends Function<T, R> {
   @Override
   R apply(T var1);

   default <V> NonNullFunction<T, V> andThen(NonNullFunction<? super R, ? extends V> after) {
      Objects.requireNonNull(after);
      return t -> (V)after.apply(this.apply(t));
   }
}

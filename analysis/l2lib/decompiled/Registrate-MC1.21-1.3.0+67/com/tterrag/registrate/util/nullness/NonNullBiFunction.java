package com.tterrag.registrate.util.nullness;

import java.util.function.BiFunction;

@FunctionalInterface
public interface NonNullBiFunction<T, U, R> extends BiFunction<T, U, R> {
   @Override
   R apply(T var1, U var2);
}

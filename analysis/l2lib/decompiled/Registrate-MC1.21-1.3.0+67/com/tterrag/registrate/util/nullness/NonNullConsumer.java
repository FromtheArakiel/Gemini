package com.tterrag.registrate.util.nullness;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface NonNullConsumer<T> extends Consumer<T> {
   @Override
   void accept(T var1);

   default NonNullConsumer<T> andThen(NonNullConsumer<? super T> after) {
      Objects.requireNonNull(after);
      return t -> {
         this.accept(t);
         after.accept(t);
      };
   }

   static <T> NonNullConsumer<T> noop() {
      return t -> {};
   }
}

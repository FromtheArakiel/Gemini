package dev.xkmc.l2core.init.reg.registrate;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Holder.Kind;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public interface LegacyHolder<T> extends Holder<T>, Supplier<T> {
   ResourceKey<T> key();

   Holder<T> holder();

   default T value() {
      return this.get();
   }

   default boolean isBound() {
      return this.holder().isBound();
   }

   default boolean is(ResourceLocation id) {
      return this.key().location().equals(id);
   }

   default boolean is(ResourceKey<T> id) {
      return this.key().equals(id);
   }

   default boolean is(Predicate<ResourceKey<T>> pred) {
      return pred.test(this.key());
   }

   default boolean is(TagKey<T> tag) {
      return this.holder().is(tag);
   }

   @Deprecated
   default boolean is(Holder<T> holder) {
      return this.holder().is(holder);
   }

   default Stream<TagKey<T>> tags() {
      return this.holder().tags();
   }

   default Either<ResourceKey<T>, T> unwrap() {
      return Either.left(this.key());
   }

   default Optional<ResourceKey<T>> unwrapKey() {
      return Optional.of(this.key());
   }

   default Kind kind() {
      return Kind.REFERENCE;
   }

   default Holder<T> getDelegate() {
      return this.holder().getDelegate();
   }

   default boolean canSerializeIn(HolderOwner<T> pOwner) {
      return true;
   }
}

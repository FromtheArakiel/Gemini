package dev.xkmc.l2core.init.reg.ench;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Holder.Kind;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public record DataGenHolder<T>(ResourceKey<T> key, T val) implements Holder<T> {
   public T value() {
      return this.val;
   }

   public boolean isBound() {
      return true;
   }

   public boolean is(ResourceLocation pLocation) {
      return false;
   }

   public boolean is(ResourceKey<T> pResourceKey) {
      return false;
   }

   public boolean is(Predicate<ResourceKey<T>> pPredicate) {
      return false;
   }

   public boolean is(TagKey<T> pTagKey) {
      return false;
   }

   public boolean is(Holder<T> pHolder) {
      return false;
   }

   public Stream<TagKey<T>> tags() {
      return Stream.empty();
   }

   public Either<ResourceKey<T>, T> unwrap() {
      return Either.left(this.key());
   }

   public Optional<ResourceKey<T>> unwrapKey() {
      return Optional.of(this.key);
   }

   public Kind kind() {
      return Kind.REFERENCE;
   }

   public boolean canSerializeIn(HolderOwner<T> pOwner) {
      return true;
   }
}

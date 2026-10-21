package dev.xkmc.l2core.init.reg.ench;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public record FakeRegistryLookup<T>(ResourceKey<? extends Registry<? extends T>> key) implements RegistryLookup<T> {
   public Lifecycle registryLifecycle() {
      return Lifecycle.stable();
   }

   public Stream<Reference<T>> listElements() {
      return Stream.empty();
   }

   public Stream<Named<T>> listTags() {
      return Stream.empty();
   }

   public Optional<Reference<T>> get(ResourceKey<T> pResourceKey) {
      return Optional.empty();
   }

   public Optional<Named<T>> get(TagKey<T> pTagKey) {
      return Optional.empty();
   }

   public boolean canSerializeIn(HolderOwner<T> pOwner) {
      return true;
   }
}

package dev.xkmc.l2core.init.reg.ench;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;

public interface HolderSetBuilder<T> {
   static <T> HolderSetBuilder<T> tag(TagKey<T> tag) {
      return new HolderSetBuilder.Simple<>(tag);
   }

   static <T> HolderSetBuilder<T> direct(ResourceKey<T> tag) {
      return new HolderSetBuilder.Direct<>(tag);
   }

   static <T> HolderSetBuilder<T> or(List<HolderSetBuilder<T>> tag) {
      return new HolderSetBuilder.Or<>(tag);
   }

   static <T> HolderSetBuilder<T> and(List<HolderSetBuilder<T>> tag) {
      return new HolderSetBuilder.And<>(tag);
   }

   static <T> HolderSetBuilder<T> not(HolderSetBuilder<T> tag) {
      return new HolderSetBuilder.Not<>(tag);
   }

   static <T> HolderSetBuilder<T> any() {
      return new HolderSetBuilder.Any<>();
   }

   HolderSet<T> build(FakeRegistryLookup<T> var1, HolderGetter<T> var2);

   record And<T>(List<HolderSetBuilder<T>> list) implements HolderSetBuilder<T> {
      @Override
      public HolderSet<T> build(FakeRegistryLookup<T> reg, HolderGetter<T> pvd) {
         return new AndHolderSet(this.list.stream().map(e -> e.build(reg, pvd)).toList());
      }
   }

   record Any<T>() implements HolderSetBuilder<T> {
      @Override
      public HolderSet<T> build(FakeRegistryLookup<T> reg, HolderGetter<T> pvd) {
         return new AnyHolderSet(reg);
      }
   }

   record Direct<T>(ResourceKey<T> key) implements HolderSetBuilder<T> {
      @Override
      public HolderSet<T> build(FakeRegistryLookup<T> reg, HolderGetter<T> pvd) {
         return HolderSet.direct(new Holder[]{pvd.getOrThrow(this.key)});
      }
   }

   record Not<T>(HolderSetBuilder<T> val) implements HolderSetBuilder<T> {
      @Override
      public HolderSet<T> build(FakeRegistryLookup<T> reg, HolderGetter<T> pvd) {
         return new NotHolderSet(reg, this.val.build(reg, pvd));
      }
   }

   record Or<T>(List<HolderSetBuilder<T>> list) implements HolderSetBuilder<T> {
      @Override
      public HolderSet<T> build(FakeRegistryLookup<T> reg, HolderGetter<T> pvd) {
         return new OrHolderSet(this.list.stream().map(e -> e.build(reg, pvd)).toList());
      }
   }

   record Simple<T>(TagKey<T> tag) implements HolderSetBuilder<T> {
      @Override
      public HolderSet<T> build(FakeRegistryLookup<T> reg, HolderGetter<T> pvd) {
         return pvd.getOrThrow(this.tag);
      }
   }
}

package com.tterrag.registrate.util;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class CreativeModeTabModifier implements Output {
   private final Supplier<FeatureFlagSet> flags;
   private final BooleanSupplier hasPermissions;
   private final BiConsumer<ItemStack, TabVisibility> acceptFunc;
   private final Supplier<ItemDisplayParameters> parameters;

   @Internal
   public CreativeModeTabModifier(
      Supplier<FeatureFlagSet> flags,
      BooleanSupplier hasPermissions,
      BiConsumer<ItemStack, TabVisibility> acceptFunc,
      Supplier<ItemDisplayParameters> parameters
   ) {
      this.flags = flags;
      this.hasPermissions = hasPermissions;
      this.acceptFunc = acceptFunc;
      this.parameters = parameters;
   }

   public FeatureFlagSet getFlags() {
      return this.flags.get();
   }

   public ItemDisplayParameters getParameters() {
      return this.parameters.get();
   }

   public boolean hasPermissions() {
      return this.hasPermissions.getAsBoolean();
   }

   public void accept(ItemStack stack, TabVisibility visibility) {
      this.acceptFunc.accept(stack, visibility);
   }

   public void accept(Supplier<? extends ItemLike> item, TabVisibility visibility) {
      this.accept(item.get(), visibility);
   }

   public void accept(Supplier<? extends ItemLike> item) {
      this.accept(item.get());
   }
}

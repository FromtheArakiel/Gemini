package com.tterrag.registrate.util;

import com.google.common.collect.ObjectArrays;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

public final class DataIngredient {
   private final Ingredient parent;
   private final ResourceLocation id;
   private final Function<RegistrateRecipeProvider, Criterion<TriggerInstance>> criteriaFactory;

   private DataIngredient(Ingredient parent, ItemLike item) {
      this.parent = parent;
      this.id = BuiltInRegistries.ITEM.getKey(item.asItem());
      this.criteriaFactory = prov -> RegistrateRecipeProvider.has(item);
   }

   private DataIngredient(Ingredient parent, TagKey<Item> tag) {
      this.parent = parent;
      this.id = tag.location();
      this.criteriaFactory = prov -> RegistrateRecipeProvider.has(tag);
   }

   private DataIngredient(Ingredient parent, ResourceLocation id, ItemPredicate... predicates) {
      this.parent = parent;
      this.id = id;
      this.criteriaFactory = prov -> RegistrateRecipeProvider.inventoryTrigger(predicates);
   }

   public Criterion<TriggerInstance> getCriterion(RegistrateRecipeProvider prov) {
      return this.criteriaFactory.apply(prov);
   }

   @SafeVarargs
   public static <T extends ItemLike> DataIngredient items(NonNullSupplier<? extends T> first, NonNullSupplier<? extends T>... others) {
      return items(first.get(), Arrays.stream(others).map(Supplier::get).toArray(ItemLike[]::new));
   }

   @SafeVarargs
   public static <T extends ItemLike> DataIngredient items(T first, T... others) {
      return ingredient(Ingredient.of((ItemLike[])ObjectArrays.concat(first, others)), first);
   }

   public static DataIngredient stacks(ItemStack first, ItemStack... others) {
      return ingredient(Ingredient.of((ItemStack[])ObjectArrays.concat(first, others)), first.getItem());
   }

   public static DataIngredient tag(TagKey<Item> tag) {
      return ingredient(Ingredient.of(tag), tag);
   }

   public static DataIngredient ingredient(Ingredient parent, ItemLike required) {
      return new DataIngredient(parent, required);
   }

   public static DataIngredient ingredient(Ingredient parent, TagKey<Item> required) {
      return new DataIngredient(parent, required);
   }

   public static DataIngredient ingredient(Ingredient parent, ResourceLocation id, ItemPredicate... criteria) {
      return new DataIngredient(parent, id, criteria);
   }

   public Ingredient toVanilla() {
      return this.parent;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean test(ItemStack p_43914_) {
      return this.parent.test(p_43914_);
   }

   public IntList getStackingIds() {
      return this.parent.getStackingIds();
   }

   public boolean isEmpty() {
      return this.parent.isEmpty();
   }

   public boolean hasNoItems() {
      return this.parent.hasNoItems();
   }

   public boolean isSimple() {
      return this.parent.isSimple();
   }

   public ICustomIngredient getCustomIngredient() {
      return this.parent.getCustomIngredient();
   }

   public boolean isCustom() {
      return this.parent.isCustom();
   }

   public Predicate<ItemStack> and(Predicate<? super ItemStack> other) {
      return this.parent.and(other);
   }

   public Predicate<ItemStack> negate() {
      return this.parent.negate();
   }

   public Predicate<ItemStack> or(Predicate<? super ItemStack> other) {
      return this.parent.or(other);
   }

   private interface Excludes {
      void toNetwork(FriendlyByteBuf var1);

      boolean checkInvalidation();

      void markValid();

      boolean isVanilla();

      ItemStack[] getItems();

      Value[] getValues();
   }
}

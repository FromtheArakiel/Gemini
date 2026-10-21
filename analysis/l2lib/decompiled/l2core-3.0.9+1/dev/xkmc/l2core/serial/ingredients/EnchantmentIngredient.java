package dev.xkmc.l2core.serial.ingredients;

import dev.xkmc.l2core.init.L2LibReg;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record EnchantmentIngredient(Holder<Enchantment> enchantment, int minLevel) implements ICustomIngredient {
   public static Ingredient of(Holder<Enchantment> ench, int min) {
      return new EnchantmentIngredient(ench, min).toVanilla();
   }

   public static Ingredient of(Provider pvd, ResourceKey<Enchantment> ench, int min) {
      Reference<Enchantment> holder = ((RegistryLookup)pvd.lookup(Registries.ENCHANTMENT).orElseThrow()).getOrThrow(ench);
      return of(holder, min);
   }

   public Stream<ItemStack> getItems() {
      Enchantment ench = (Enchantment)this.enchantment.value();
      return IntStream.range(this.minLevel, ench.getMaxLevel() + 1)
         .mapToObj(i -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance(this.enchantment, i)));
   }

   public boolean isSimple() {
      return false;
   }

   public IngredientType<?> getType() {
      return L2LibReg.ING_ENCH.get();
   }

   public boolean test(ItemStack stack) {
      return EnchantmentHelper.getEnchantmentsForCrafting(stack).getLevel(this.enchantment()) >= this.minLevel();
   }
}

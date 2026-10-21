package dev.xkmc.l2core.serial.ingredients;

import dev.xkmc.l2core.init.L2LibReg;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record PotionIngredient(Holder<Potion> potion) implements ICustomIngredient {
   public static Ingredient of(Holder<Potion> potion) {
      return new PotionIngredient(potion).toVanilla();
   }

   public Stream<ItemStack> getItems() {
      ItemStack stack = new ItemStack(Items.POTION);
      stack.set(DataComponents.POTION_CONTENTS, new PotionContents(this.potion));
      return Stream.of(stack);
   }

   public boolean isSimple() {
      return false;
   }

   public IngredientType<?> getType() {
      return L2LibReg.ING_POTION.get();
   }

   public boolean test(ItemStack stack) {
      PotionContents val = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
      return val != null && val.is(this.potion);
   }
}

package dev.xkmc.l2core.serial.recipe;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public record DataRecipeWrapper(RecipeOutput pvd, ItemStack stack) implements RecipeOutput {
   public static RecipeOutput of(RecipeOutput pvd, ItemStack stack) {
      return new DataRecipeWrapper(pvd, stack);
   }

   public Builder advancement() {
      return this.pvd.advancement();
   }

   public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
      if (recipe instanceof ShapedRecipe r) {
         r.result.applyComponents(this.stack.getComponents());
      }

      if (recipe instanceof ShapelessRecipe r) {
         r.result.applyComponents(this.stack.getComponents());
      }

      this.pvd.accept(id, recipe, advancement, conditions);
   }
}

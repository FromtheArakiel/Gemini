package dev.xkmc.l2core.serial.recipe;

import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class CustomSmithingBuilder<T extends AbstractSmithingRecipe<T>> extends SmithingTransformRecipeBuilder {
   private final AbstractSmithingRecipe.RecipeFactory<T> factory;

   public CustomSmithingBuilder(AbstractSmithingRecipe.RecipeFactory<T> factory, Ingredient template, Ingredient base, Ingredient add, Item result) {
      super(template, base, add, RecipeCategory.MISC, result);
      this.factory = factory;
   }

   public void save(RecipeOutput pRecipeOutput, ResourceLocation pRecipeId) {
      this.ensureValid(pRecipeId);
      Builder advancement$builder = pRecipeOutput.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(pRecipeId))
         .requirements(Strategy.OR);
      this.criteria.forEach(advancement$builder::addCriterion);
      SmithingTransformRecipe smithingtransformrecipe = new SmithingTransformRecipe(this.template, this.base, this.addition, new ItemStack(this.result));
      pRecipeOutput.accept(
         pRecipeId,
         this.factory.map(smithingtransformrecipe),
         advancement$builder.build(pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"))
      );
   }
}

package dev.xkmc.l2core.serial.recipe;

import java.util.Objects;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

public class CustomShapelessBuilder<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipeBuilder {
   private final AbstractShapelessRecipe.RecipeFactory<T> factory;

   public CustomShapelessBuilder(AbstractShapelessRecipe.RecipeFactory<T> factory, ItemLike result, int count) {
      super(RecipeCategory.MISC, result, count);
      this.factory = factory;
   }

   public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
      this.ensureValid(pId);
      Builder advancement$builder = pRecipeOutput.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(pId))
         .requirements(Strategy.OR);
      this.criteria.forEach(advancement$builder::addCriterion);
      ShapelessRecipe shapelessrecipe = new ShapelessRecipe(
         Objects.requireNonNullElse(this.group, ""),
         RecipeBuilder.determineBookCategory(this.category),
         new ItemStack(this.result, this.count),
         this.ingredients
      );
      pRecipeOutput.accept(pId, this.factory.map(shapelessrecipe), advancement$builder.build(pId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }
}

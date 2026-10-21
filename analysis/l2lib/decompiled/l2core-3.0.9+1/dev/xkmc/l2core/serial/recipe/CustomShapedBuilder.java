package dev.xkmc.l2core.serial.recipe;

import java.util.Objects;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

public class CustomShapedBuilder<T extends AbstractShapedRecipe<T>> extends ShapedRecipeBuilder {
   private final AbstractShapedRecipe.RecipeFactory<T> factory;

   public CustomShapedBuilder(AbstractShapedRecipe.RecipeFactory<T> factory, ItemLike result, int count) {
      super(RecipeCategory.MISC, result, count);
      this.factory = factory;
   }

   public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
      ShapedRecipePattern shapedrecipepattern = this.ensureValid(pId);
      Builder advancement$builder = pRecipeOutput.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(pId))
         .requirements(Strategy.OR);
      this.criteria.forEach(advancement$builder::addCriterion);
      ShapedRecipe shapedrecipe = new ShapedRecipe(
         Objects.requireNonNullElse(this.group, ""),
         RecipeBuilder.determineBookCategory(this.category),
         shapedrecipepattern,
         new ItemStack(this.result, this.count),
         this.showNotification
      );
      T rec = this.factory.map(shapedrecipe);
      pRecipeOutput.accept(pId, rec, advancement$builder.build(pId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }
}

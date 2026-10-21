package dev.xkmc.l2core.serial.recipe;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.Nullable;

public class BaseRecipeBuilder<T extends BaseRecipeBuilder<T, Rec, SRec, Inv>, Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends RecipeInput>
   implements RecipeBuilder {
   protected final BaseRecipe.RecType<Rec, SRec, Inv> type;
   protected final Rec recipe;
   protected final Item result;
   protected final Builder advancement = Builder.advancement();
   protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

   public BaseRecipeBuilder(BaseRecipe.RecType<Rec, SRec, Inv> type, Item result) {
      this.type = type;
      this.recipe = (Rec)((BaseRecipe)type.blank());
      this.result = result;
   }

   public T getThis() {
      return (T)this;
   }

   public T unlockedBy(String name, Criterion<?> trigger) {
      this.criteria.put(name, trigger);
      return this.getThis();
   }

   public T group(@Nullable String pGroupName) {
      return this.getThis();
   }

   public Item getResult() {
      return this.result;
   }

   public void save(RecipeOutput pvd, ResourceLocation id) {
      Builder builder = pvd.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(id))
         .requirements(Strategy.OR);
      this.criteria.forEach(builder::addCriterion);
      id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), BuiltInRegistries.RECIPE_SERIALIZER.getKey(this.type).getPath() + "/" + id.getPath());
      pvd.accept(id, this.recipe, builder.build(id));
   }
}

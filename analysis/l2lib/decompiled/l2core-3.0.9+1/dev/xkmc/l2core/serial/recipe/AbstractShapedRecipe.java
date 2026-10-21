package dev.xkmc.l2core.serial.recipe;

import com.mojang.serialization.MapCodec;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractShapedRecipe<T extends AbstractShapedRecipe<T>> extends ShapedRecipe {
   public AbstractShapedRecipe(String group, ShapedRecipePattern pattern, ItemStack result) {
      super(group, CraftingBookCategory.MISC, pattern, result);
   }

   public abstract AbstractShapedRecipe.Serializer<T> getSerializer();

   @FunctionalInterface
   public interface RecipeFactory<T extends AbstractShapedRecipe<T>> {
      T create(String var1, ShapedRecipePattern var2, ItemStack var3);

      default T map(ShapedRecipe r) {
         return this.create(r.getGroup(), r.pattern, r.result);
      }
   }

   public static class Serializer<T extends AbstractShapedRecipe<T>> extends net.minecraft.world.item.crafting.ShapedRecipe.Serializer {
      private final AbstractShapedRecipe.RecipeFactory<T> factory;

      public Serializer(AbstractShapedRecipe.RecipeFactory<T> factory) {
         this.factory = factory;
      }

      public MapCodec<ShapedRecipe> codec() {
         return super.codec().xmap(this.factory::map, e -> e);
      }

      public StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> streamCodec() {
         return super.streamCodec().map(this.factory::map, e -> e);
      }
   }
}

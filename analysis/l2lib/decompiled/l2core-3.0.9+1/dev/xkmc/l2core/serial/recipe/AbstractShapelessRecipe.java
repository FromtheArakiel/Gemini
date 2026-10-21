package dev.xkmc.l2core.serial.recipe;

import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractShapelessRecipe<T extends AbstractShapelessRecipe<T>> extends ShapelessRecipe {
   public AbstractShapelessRecipe(String group, ItemStack result, NonNullList<Ingredient> ingredients) {
      super(group, CraftingBookCategory.MISC, result, ingredients);
   }

   public List<ItemStack> getJEIResult() {
      return List.of(this.result);
   }

   public abstract AbstractShapelessRecipe.Serializer<T> getSerializer();

   @FunctionalInterface
   public interface RecipeFactory<T extends AbstractShapelessRecipe<T>> {
      T create(String var1, ItemStack var2, NonNullList<Ingredient> var3);

      default T map(ShapelessRecipe r) {
         return this.create(r.getGroup(), r.result, r.getIngredients());
      }
   }

   public static class Serializer<T extends AbstractShapelessRecipe<T>> extends net.minecraft.world.item.crafting.ShapelessRecipe.Serializer {
      private final AbstractShapelessRecipe.RecipeFactory<T> factory;

      public Serializer(AbstractShapelessRecipe.RecipeFactory<T> factory) {
         this.factory = factory;
      }

      public MapCodec<ShapelessRecipe> codec() {
         return super.codec().xmap(this.factory::map, e -> e);
      }

      public StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> streamCodec() {
         return super.streamCodec().map(this.factory::map, e -> e);
      }
   }
}

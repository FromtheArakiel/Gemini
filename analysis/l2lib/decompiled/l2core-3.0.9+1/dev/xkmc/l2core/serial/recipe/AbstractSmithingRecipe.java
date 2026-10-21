package dev.xkmc.l2core.serial.recipe;

import com.mojang.serialization.MapCodec;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.ItemLike;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractSmithingRecipe<T extends AbstractSmithingRecipe<T>> extends SmithingTransformRecipe {
   public static final Ingredient TEMPLATE_PLACEHOLDER = Ingredient.of(new ItemLike[]{Items.PAPER});

   public AbstractSmithingRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
      super(template, base, addition, result);
   }

   public abstract AbstractSmithingRecipe.Serializer<T> getSerializer();

   @FunctionalInterface
   public interface RecipeFactory<T extends AbstractSmithingRecipe<T>> {
      T create(Ingredient var1, Ingredient var2, Ingredient var3, ItemStack var4);

      default T map(SmithingTransformRecipe r) {
         return this.create(r.template, r.base, r.addition, r.result);
      }
   }

   public static class Serializer<T extends AbstractSmithingRecipe<T>> extends net.minecraft.world.item.crafting.SmithingTransformRecipe.Serializer {
      private final AbstractSmithingRecipe.RecipeFactory<T> factory;

      public Serializer(AbstractSmithingRecipe.RecipeFactory<T> factory) {
         this.factory = factory;
      }

      public MapCodec<SmithingTransformRecipe> codec() {
         return super.codec().xmap(this.factory::map, e -> e);
      }

      public StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> streamCodec() {
         return super.streamCodec().map(this.factory::map, e -> e);
      }
   }
}

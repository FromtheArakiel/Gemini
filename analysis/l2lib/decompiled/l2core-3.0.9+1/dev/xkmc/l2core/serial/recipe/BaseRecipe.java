package dev.xkmc.l2core.serial.recipe;

import java.util.function.Supplier;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public abstract class BaseRecipe<Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends RecipeInput> implements Recipe<Inv> {
   private final BaseRecipe.RecType<Rec, SRec, Inv> factory;

   public BaseRecipe(BaseRecipe.RecType<Rec, SRec, Inv> fac) {
      this.factory = fac;
   }

   public abstract boolean matches(Inv var1, Level var2);

   public abstract ItemStack assemble(Inv var1, Provider var2);

   public abstract ItemStack getResultItem(Provider var1);

   public abstract boolean canCraftInDimensions(int var1, int var2);

   public final RecipeSerializer<?> getSerializer() {
      return this.factory;
   }

   public final RecipeType<?> getType() {
      return this.factory.type.get();
   }

   public static class RecType<Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends RecipeInput> extends RecSerializer<Rec, Inv> {
      public final Supplier<RecipeType<SRec>> type;

      public RecType(Class<Rec> rec, Supplier<RecipeType<SRec>> type) {
         super(rec);
         this.type = type;
      }
   }
}

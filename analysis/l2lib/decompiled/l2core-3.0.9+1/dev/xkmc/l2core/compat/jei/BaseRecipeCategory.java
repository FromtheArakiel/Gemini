package dev.xkmc.l2core.compat.jei;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BaseRecipeCategory<T, C extends BaseRecipeCategory<T, C>> implements IRecipeCategory<T> {
   private final mezz.jei.api.recipe.RecipeType<T> type;
   protected IDrawable background;
   protected IDrawable icon;

   public static <T extends R, R> Class<T> cast(Class<R> cls) {
      return cls;
   }

   public BaseRecipeCategory(ResourceLocation name, Class<T> cls) {
      this.type = new mezz.jei.api.recipe.RecipeType(name, cls);
   }

   public <R extends Recipe<I>, I extends RecipeInput> List<R> getAll(RecipeType<R> type) {
      ClientLevel level = Minecraft.getInstance().level;
      return level == null ? List.of() : level.getRecipeManager().getAllRecipesFor(type).stream().map(RecipeHolder::value).toList();
   }

   public final C getThis() {
      return (C)this;
   }

   public final mezz.jei.api.recipe.RecipeType<T> getRecipeType() {
      return this.type;
   }

   public final IDrawable getBackground() {
      return this.background;
   }

   public final IDrawable getIcon() {
      return this.icon;
   }

   public abstract void setRecipe(IRecipeLayoutBuilder var1, T var2, IFocusGroup var3);
}

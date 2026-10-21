package dev.xkmc.l2core.compat.patchouli;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.DataIngredient;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.serial.advancements.RewardBuilder;
import dev.xkmc.l2core.serial.recipe.ConditionalRecipeWrapper;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliDataComponents;
import vazkii.patchouli.common.item.PatchouliItems;

public class PatchouliHelper {
   public static final ProviderType<PatchouliProvider> PATCHOULI = ProviderType.registerServerData("patchouli", PatchouliProvider::new);
   private final L2Registrate reg;
   public final ResourceLocation book;
   public final RewardBuilder reward;
   private ResourceLocation model;

   public static ItemStack getBook(ResourceLocation book) {
      return ItemModBook.forBook(book);
   }

   public static Builder getBookLoot(ResourceLocation book) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool().add(LootItem.lootTableItem(PatchouliItems.BOOK).apply(SetComponentsFunction.setComponent(PatchouliDataComponents.BOOK, book)))
         );
   }

   public PatchouliHelper(L2Registrate reg, String name) {
      this.reg = reg;
      this.book = reg.loc(name);
      this.reward = new RewardBuilder(reg, 0, ResourceKey.create(Registries.LOOT_TABLE, this.book), () -> getBookLoot(this.book));
   }

   public PatchouliHelper buildModel() {
      return this.buildModel("book");
   }

   public PatchouliHelper buildModel(String path) {
      this.model = ResourceLocation.fromNamespaceAndPath(this.reg.getModid(), path);
      this.reg
         .addDataGenerator(
            ProviderType.ITEM_MODEL,
            pvd -> ((ItemModelBuilder)((ItemModelBuilder)pvd.getBuilder(path)).parent(new UncheckedModelFile("item/generated")))
               .texture("layer0", "item/" + path)
         );
      return this;
   }

   public PatchouliHelper buildShapelessRecipe(Consumer<ShapelessRecipeBuilder> cons, Supplier<Item> unlock) {
      return this.buildRecipe(() -> (RecipeBuilder)Util.make(new ShapelessRecipeBuilder(RecipeCategory.MISC, getBook(this.book)), cons), unlock);
   }

   public PatchouliHelper buildShapedRecipe(Consumer<ShapedRecipeBuilder> cons, Supplier<Item> unlock) {
      return this.buildRecipe(() -> (RecipeBuilder)Util.make(new ShapedRecipeBuilder(RecipeCategory.MISC, getBook(this.book)), cons), unlock);
   }

   private PatchouliHelper buildRecipe(Supplier<RecipeBuilder> cons, Supplier<Item> unlock) {
      this.reg.addDataGenerator(ProviderType.RECIPE, pvd -> {
         RecipeBuilder builder = cons.get();
         builder.unlockedBy("has_" + pvd.safeName((ItemLike)unlock.get()), DataIngredient.items(unlock.get(), new Item[0]).getCriterion(pvd));
         builder.save(ConditionalRecipeWrapper.mod(pvd, "patchouli"), ResourceLocation.fromNamespaceAndPath(this.reg.getModid(), "book"));
      });
      return this;
   }

   public PatchouliHelper buildBook(String title, String landing, int ver, ResourceKey<CreativeModeTab> tab) {
      if (this.model == null) {
         throw new IllegalStateException("Patchouli Book must have a model first");
      }

      String titleId = "patchouli." + this.reg.getModid() + ".title";
      String descId = "patchouli." + this.reg.getModid() + ".landing";
      this.reg.addRawLang(titleId, title);
      this.reg.addRawLang(descId, landing);
      this.reg
         .addDataGenerator(
            PATCHOULI,
            pvd -> pvd.accept(
               this.reg.getModid() + "/patchouli_books/" + this.book.getPath() + "/book",
               new PatchouliHelper.BookEntry(titleId, descId, ver, this.model, tab.location(), true)
            )
         );
      return this;
   }

   public record BookEntry(String name, String landing_text, int version, ResourceLocation model, ResourceLocation creative_tab, boolean use_resource_pack) {
   }
}

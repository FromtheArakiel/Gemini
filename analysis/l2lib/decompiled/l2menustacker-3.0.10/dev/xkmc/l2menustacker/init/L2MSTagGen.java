package dev.xkmc.l2menustacker.init;

import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class L2MSTagGen {
   public static final TagKey<Item> QUICK_ACCESS = ItemTags.create(L2MenuStacker.loc("quick_access"));
   public static final TagKey<Item> QUICK_ACCESS_VANILLA = ItemTags.create(L2MenuStacker.loc("quick_access_vanilla"));

   public static void genItemTags(RegistrateItemTagsProvider pvd) {
      pvd.addTag(QUICK_ACCESS_VANILLA)
         .add(new Item[]{Items.CRAFTING_TABLE, Items.SMITHING_TABLE, Items.STONECUTTER, Items.GRINDSTONE, Items.CARTOGRAPHY_TABLE, Items.LOOM});
      pvd.addTag(QUICK_ACCESS).addTag(QUICK_ACCESS_VANILLA);
   }
}

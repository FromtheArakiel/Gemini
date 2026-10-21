package dev.xkmc.l2itemselector.select.item;

import dev.xkmc.l2itemselector.init.data.L2ISTagGen;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;

public class ItemSelector extends IItemSelector {
   public static final List<ItemSelector> SELECTORS = new ArrayList<>();
   private final List<ItemStack> list;
   private final Set<Item> set = new HashSet<>();

   public ItemSelector(ResourceLocation rl, ItemStack... stacks) {
      super(rl);
      this.list = List.of(stacks);

      for (ItemStack stack : stacks) {
         if (!stack.is(L2ISTagGen.SELECTABLE)) {
            LogManager.getLogger().error("item " + stack.getItem() + " is not marked as selectable");
         }

         this.set.add(stack.getItem());
      }

      SELECTORS.add(this);
   }

   @Override
   public boolean test(ItemStack stack) {
      return this.set.contains(stack.getItem());
   }

   @Override
   public int getIndex(Player player, ItemStack stack) {
      for (int i = 0; i < this.list.size(); i++) {
         if (this.test(player.getMainHandItem())) {
            if (this.list.get(i).getItem() == player.getMainHandItem().getItem()) {
               return i;
            }
         } else if (this.test(player.getOffhandItem()) && this.list.get(i).getItem() == player.getMainHandItem().getItem()) {
            return i;
         }
      }

      return 0;
   }

   @Override
   public List<ItemStack> getList(ItemStack stack) {
      return this.list;
   }
}

package dev.xkmc.l2itemselector.select.item;

import dev.xkmc.l2itemselector.init.data.L2ISTagGen;
import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemConvertor {
   private static ItemStack find(List<ItemStack> ans, Inventory inv) {
      for (ItemStack choice : ans) {
         if (inv.hasRemainingSpaceForItem(inv.getItem(inv.selected), choice)) {
            return choice;
         }
      }

      for (ItemStack choice : ans) {
         if (inv.hasRemainingSpaceForItem(inv.getItem(40), choice)) {
            return choice;
         }
      }

      for (ItemStack choice : ans) {
         for (int i = 0; i < inv.items.size(); i++) {
            if (inv.hasRemainingSpaceForItem((ItemStack)inv.items.get(i), choice)) {
               return choice;
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public static ItemStack convert(ItemStack stack, Player player) {
      if (!stack.is(L2ISTagGen.SELECTABLE)) {
         return stack;
      }

      ItemSelector ans = null;

      for (ItemSelector selector : ItemSelector.SELECTORS) {
         if (selector.test(stack)) {
            ans = selector;
            break;
         }
      }

      if (ans == null) {
         return stack;
      }

      Inventory inv = player.getInventory();
      List<ItemStack> list = ans.getList(stack);
      ItemStack old = find(List.of(stack), inv);
      if (!old.isEmpty()) {
         return stack;
      }

      ItemStack found = find(list, inv);
      if (found.isEmpty()) {
         return stack;
      }

      ItemStack result = found.copy();
      result.setCount(stack.getCount());
      return result;
   }
}

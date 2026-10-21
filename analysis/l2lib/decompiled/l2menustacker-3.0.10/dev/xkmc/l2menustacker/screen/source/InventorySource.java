package dev.xkmc.l2menustacker.screen.source;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InventorySource extends ItemSource<SimpleSlotData> {
   public ItemStack getItem(Player player, SimpleSlotData data) {
      return player.getInventory().getItem(data.slot());
   }
}

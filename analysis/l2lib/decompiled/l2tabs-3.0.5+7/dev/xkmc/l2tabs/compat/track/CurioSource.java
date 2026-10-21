package dev.xkmc.l2tabs.compat.track;

import dev.xkmc.l2menustacker.screen.source.ItemSource;
import dev.xkmc.l2tabs.compat.api.AccessoriesMultiplex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CurioSource extends ItemSource<CurioSlotData> {
   public ItemStack getItem(Player player, CurioSlotData data) {
      return AccessoriesMultiplex.getOptional().map(e -> e.getItemFromSlot(player, data)).orElse(ItemStack.EMPTY);
   }
}

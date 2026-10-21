package dev.xkmc.l2menustacker.click;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public abstract class ReadOnlyStackClickHandler extends SlotClickHandler {
   public ReadOnlyStackClickHandler(ResourceLocation rl) {
      super(rl);
   }

   @Override
   public void handle(ServerPlayer player, int index, int slot, int wid) {
      AbstractContainerMenu menu = player.containerMenu;
      if (slot >= 0) {
         this.handle(player, player.getInventory().getItem(slot));
      } else {
         if (wid == 0 || menu.containerId == 0 || wid != menu.containerId) {
            return;
         }

         this.handle(player, menu.getSlot(index).getItem());
      }
   }

   protected abstract void handle(ServerPlayer var1, ItemStack var2);
}

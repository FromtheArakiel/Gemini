package dev.xkmc.l2menustacker.click.writable;

import dev.xkmc.l2menustacker.click.SlotClickHandler;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class WritableStackClickHandler extends SlotClickHandler {
   public WritableStackClickHandler(ResourceLocation rl) {
      super(rl);
   }

   @Nullable
   protected ClickedPlayerSlotResult getSlot(ServerPlayer player, int index, int slot, int wid) {
      if (slot >= 0) {
         return new ClickedPlayerSlotResult(player.getInventory().getItem(slot), PlayerSlot.ofInventory(slot), new NoContainerCallback());
      } else {
         AbstractContainerMenu menu = player.containerMenu;
         if (wid != 0 && menu.containerId != 0 && wid == menu.containerId) {
            PlayerSlot<?> playerSlot = PlayerSlot.ofOtherInventory(slot, index, wid, menu);
            return playerSlot == null
               ? null
               : new ClickedPlayerSlotResult(menu.getSlot(index).getItem(), playerSlot, new RealContainerCallback(menu.getSlot(index).container));
         } else {
            return null;
         }
      }
   }

   protected abstract void handle(ServerPlayer var1, ClickedPlayerSlotResult var2);

   @Override
   public void handle(ServerPlayer player, int index, int slot, int wid) {
      ClickedPlayerSlotResult result = this.getSlot(player, index, slot, wid);
      if (result != null) {
         this.handle(player, result);
      }
   }
}

package dev.xkmc.l2menustacker.compat.arclight;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class AnvilMenuArclight extends AnvilMenu {
   public AnvilMenuArclight(int p_40248_, Inventory p_40249_, ContainerLevelAccess p_40250_) {
      super(p_40248_, p_40249_, p_40250_);
   }

   public boolean stillValid(Player p_39780_) {
      return true;
   }
}

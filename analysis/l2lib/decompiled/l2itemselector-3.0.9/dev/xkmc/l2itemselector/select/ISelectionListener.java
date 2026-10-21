package dev.xkmc.l2itemselector.select;

import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import java.util.function.BooleanSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface ISelectionListener {
   ResourceLocation getID();

   boolean isClientActive(Player var1);

   void handleServerSetSelection(SetSelectedToServer var1, Player var2);

   boolean handleClientScroll(int var1, Player var2);

   void handleClientKey(L2Keys var1, Player var2);

   boolean handleClientNumericKey(int var1, BooleanSupplier var2);

   default boolean scrollBypassShift() {
      return false;
   }

   default void toServer(int slot) {
      L2ItemSelector.PACKET_HANDLER.toServer(SetSelectedToServer.of(slot, this.getID()));
   }

   default boolean isHoldKeyDown(Player player) {
      return player.isShiftKeyDown();
   }

   default boolean handleClientScroll(int diff, double delta, Player player) {
      return diff == 0 ? true : this.handleClientScroll((int)Math.signum(diff), player);
   }
}

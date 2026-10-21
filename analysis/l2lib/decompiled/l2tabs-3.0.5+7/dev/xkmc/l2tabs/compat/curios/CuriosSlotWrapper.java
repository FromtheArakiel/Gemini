package dev.xkmc.l2tabs.compat.curios;

import dev.xkmc.l2tabs.compat.api.IAccessoriesSlotWrapper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

record CuriosSlotWrapper(LivingEntity player, ICurioStacksHandler cap, int index, String identifier) implements IAccessoriesSlotWrapper {
   @Override
   public Slot toSlot(int x, int y) {
      return new TabCurioSlot(this.player, this.cap.getStacks(), this.index, this.identifier, x, y, this.cap.getRenders());
   }
}

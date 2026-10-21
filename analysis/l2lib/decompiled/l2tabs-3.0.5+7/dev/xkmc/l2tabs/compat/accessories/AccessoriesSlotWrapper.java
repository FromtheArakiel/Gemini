package dev.xkmc.l2tabs.compat.accessories;

import dev.xkmc.l2tabs.compat.api.IAccessoriesSlotWrapper;
import io.wispforest.accessories.api.AccessoriesContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;

record AccessoriesSlotWrapper(LivingEntity player, AccessoriesContainer cap, int index, String identifier) implements IAccessoriesSlotWrapper {
   @Override
   public Slot toSlot(int x, int y) {
      return new TabAccessoriesSlot(this.player, this.cap, this.index, this.identifier, x, y);
   }
}

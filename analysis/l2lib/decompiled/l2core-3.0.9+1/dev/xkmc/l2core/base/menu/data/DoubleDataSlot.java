package dev.xkmc.l2core.base.menu.data;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class DoubleDataSlot {
   private final LongDataSlot data;

   public DoubleDataSlot(AbstractContainerMenu menu) {
      this.data = new LongDataSlot(menu);
   }

   public double get() {
      return Double.longBitsToDouble(this.data.get());
   }

   public void set(double pc) {
      this.data.set(Double.doubleToLongBits(pc));
   }
}

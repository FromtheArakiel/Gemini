package dev.xkmc.l2core.base.menu.base;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PredSlot extends Slot {
   private final Predicate<ItemStack> pred;
   private final int slotCache;
   @Nullable
   private BooleanSupplier pickup;
   @Nullable
   private BooleanSupplier inputLockPred;
   private int max = 64;
   private boolean changed = false;
   private boolean lockInput = false;
   private boolean lockOutput = false;

   public PredSlot(Container inv, int ind, int x, int y, Predicate<ItemStack> pred) {
      super(inv, ind, x, y);
      this.pred = pred;
      this.slotCache = ind;
   }

   public PredSlot setInputLockPred(BooleanSupplier pred) {
      this.inputLockPred = pred;
      return this;
   }

   public PredSlot setPickup(BooleanSupplier pickup) {
      this.pickup = pickup;
      return this;
   }

   public PredSlot setMax(int max) {
      this.max = max;
      return this;
   }

   public int getMaxStackSize() {
      return Math.min(this.max, super.getMaxStackSize());
   }

   public boolean mayPlace(ItemStack stack) {
      return this.isInputLocked() ? false : this.pred.test(stack);
   }

   public boolean mayPickup(Player player) {
      return this.isOutputLocked() ? false : this.pickup == null || this.pickup.getAsBoolean();
   }

   public void setChanged() {
      this.changed = true;
      super.setChanged();
   }

   public boolean clearDirty(Runnable r) {
      if (this.changed) {
         r.run();
         this.changed = false;
         return true;
      } else {
         return false;
      }
   }

   public boolean clearDirty() {
      if (this.changed) {
         this.changed = false;
         return true;
      } else {
         return false;
      }
   }

   public void updateEject(Player player) {
      if (!this.mayPlace(this.getItem())) {
         this.clearSlot(player);
      }
   }

   public void setLockInput(boolean lock) {
      this.lockInput = lock;
   }

   public boolean isInputLocked() {
      return this.lockInput || this.inputLockPred != null && this.inputLockPred.getAsBoolean();
   }

   public void setLockOutput(boolean lock) {
      this.lockOutput = lock;
   }

   public boolean isOutputLocked() {
      return this.lockOutput;
   }

   public void clearSlot(Player player) {
      BaseContainerMenu.clearSlot(player, this.container, this.slotCache);
   }
}

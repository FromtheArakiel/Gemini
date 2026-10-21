package dev.xkmc.l2core.util;

import net.minecraft.world.item.ItemStack;

public final class DCStack {
   private final ItemStack stack;
   private final int hashCode;

   public DCStack(ItemStack stack) {
      this.stack = stack;
      this.hashCode = stack.hashCode();
   }

   public ItemStack stack() {
      return this.stack;
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof DCStack s && this.hashCode == s.hashCode && this.stack.equals(s.stack);
   }
}

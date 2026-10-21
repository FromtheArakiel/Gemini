package dev.xkmc.l2tabs.compat.accessories;

import dev.xkmc.l2tabs.compat.api.INamedSlot;
import dev.xkmc.l2tabs.compat.track.CurioSlotData;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.menu.AccessoriesInternalSlot;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

class TabAccessoriesSlot extends AccessoriesInternalSlot implements INamedSlot {
   private final String identifier;
   private final LivingEntity entity;
   private final AccessoriesContainer handler;
   private final int index;

   public TabAccessoriesSlot(LivingEntity entity, AccessoriesContainer handler, int index, String identifier, int xPosition, int yPosition) {
      super(handler, false, index, xPosition, yPosition);
      this.identifier = identifier;
      this.entity = entity;
      this.handler = handler;
      this.index = index;
   }

   @Override
   public CurioSlotData toSlotData() {
      return new CurioSlotData(this.identifier, this.index);
   }

   public boolean isValid() {
      return this.handler.getSize() > this.index;
   }

   public void set(@Nonnull ItemStack stack) {
      if (this.isValid()) {
         super.set(stack);
      }
   }

   @NotNull
   public ItemStack getItem() {
      return !this.isValid() ? ItemStack.EMPTY : super.getItem();
   }

   public boolean mayPlace(@Nonnull ItemStack stack) {
      return !this.isValid() ? false : super.mayPlace(stack);
   }

   public boolean mayPickup(Player playerIn) {
      return !this.isValid() ? false : super.mayPickup(playerIn);
   }

   public void setChanged() {
      super.setChanged();
   }

   @Override
   public Component getName() {
      return (Component)this.getTooltipData().getFirst();
   }
}

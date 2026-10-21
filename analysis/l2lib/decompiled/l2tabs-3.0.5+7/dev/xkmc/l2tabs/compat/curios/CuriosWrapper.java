package dev.xkmc.l2tabs.compat.curios;

import dev.xkmc.l2tabs.compat.api.IAccessoriesSlotWrapper;
import dev.xkmc.l2tabs.compat.api.IAccessoriesWrapper;
import java.util.ArrayList;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

class CuriosWrapper extends IAccessoriesWrapper {
   private final ArrayList<CuriosSlotWrapper> list = new ArrayList<>();

   public CuriosWrapper(LivingEntity player, int page) {
      super(player);
      int max = 6;
      ICuriosItemHandler cap = (ICuriosItemHandler)player.getCapability(CuriosCapability.INVENTORY);
      this.page = page;
      if (cap == null) {
         this.total = 0;
      } else {
         int offset = page * max * 9;
         int count = 0;

         for (Entry<String, ICurioStacksHandler> ent : cap.getCurios().entrySet()) {
            ICurioStacksHandler stack = ent.getValue();
            if (stack.isVisible()) {
               for (int i = 0; i < stack.getSlots(); i++) {
                  count++;
                  if (offset > 0) {
                     offset--;
                  } else if (this.list.size() < max * 9) {
                     this.list.add(new CuriosSlotWrapper(player, stack, i, ent.getKey()));
                  }
               }
            }
         }

         this.total = (count - 1) / (max * 9) + 1;
      }
   }

   @Override
   public int getSize() {
      return this.list.size();
   }

   @Override
   public int getRows() {
      return this.list.isEmpty() ? 0 : (this.list.size() - 1) / 9 + 1;
   }

   @Nullable
   @Override
   public IAccessoriesSlotWrapper getSlotAtPosition(int i) {
      return i >= 0 && i < this.list.size() ? this.list.get(i) : null;
   }
}

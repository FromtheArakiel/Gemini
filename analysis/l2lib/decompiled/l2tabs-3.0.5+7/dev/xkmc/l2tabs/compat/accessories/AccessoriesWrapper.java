package dev.xkmc.l2tabs.compat.accessories;

import dev.xkmc.l2tabs.compat.api.IAccessoriesSlotWrapper;
import dev.xkmc.l2tabs.compat.api.IAccessoriesWrapper;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotGroup;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.data.SlotGroupLoader;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;

class AccessoriesWrapper extends IAccessoriesWrapper {
   private final ArrayList<AccessoriesSlotWrapper> list = new ArrayList<>();

   public AccessoriesWrapper(LivingEntity player, int page) {
      super(player);
      int max = 6;
      AccessoriesCapability cap = AccessoriesCapability.get(player);
      this.page = page;
      if (cap == null) {
         this.total = 0;
      } else {
         int offset = page * max * 9;
         int count = 0;
         List<AccessoriesContainer> containers = new ArrayList<>();
         List<SlotGroup> groups = SlotGroupLoader.getGroups(player.level(), true);
         Map<String, AccessoriesContainer> map = cap.getContainers();

         for (SlotGroup group : groups.stream().sorted(Comparator.comparingInt(SlotGroup::order).reversed()).toList()) {
            Set<String> slotNames = group.slots();

            for (SlotType slot : slotNames.stream()
               .map(s -> SlotTypeLoader.getSlotType(player.level(), s))
               .filter(Objects::nonNull)
               .sorted(Comparator.comparingInt(SlotType::order).reversed())
               .toList()) {
               AccessoriesContainer cont = map.get(slot.name());
               if (cont != null && cont.slotType() != null) {
                  containers.add(cont);
               }
            }
         }

         for (AccessoriesContainer cont : containers) {
            for (int i = 0; i < cont.getSize(); i++) {
               count++;
               if (offset > 0) {
                  offset--;
               } else if (this.list.size() < max * 9) {
                  this.list.add(new AccessoriesSlotWrapper(player, cont, i, cont.getSlotName()));
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

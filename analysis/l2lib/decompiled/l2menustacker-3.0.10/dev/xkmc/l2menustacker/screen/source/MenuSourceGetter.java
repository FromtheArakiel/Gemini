package dev.xkmc.l2menustacker.screen.source;

import java.util.Optional;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface MenuSourceGetter<T extends AbstractContainerMenu> {
   static <T extends AbstractContainerMenu> MenuSourceGetter<T> compound(MenuSourceGetter<T> prev, MenuSourceGetter<T> getter) {
      return (menu, slot, index, wid) -> prev.getSlot(menu, slot, index, wid).or(() -> getter.getSlot((T)menu, slot, index, wid));
   }

   Optional<PlayerSlot<?>> getSlot(T var1, int var2, int var3, int var4);
}

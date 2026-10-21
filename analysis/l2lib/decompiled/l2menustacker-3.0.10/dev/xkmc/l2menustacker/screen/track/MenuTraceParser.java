package dev.xkmc.l2menustacker.screen.track;

import java.util.Optional;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface MenuTraceParser<T extends AbstractContainerMenu> {
   static <T extends AbstractContainerMenu> MenuTraceParser<T> compound(MenuTraceParser<T> prev, MenuTraceParser<T> getter) {
      return menu -> prev.track(menu).or(() -> getter.track((T)menu));
   }

   Optional<TrackedEntry<?>> track(T var1);
}

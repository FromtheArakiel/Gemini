package dev.xkmc.l2menustacker.screen.source;

import dev.xkmc.l2serial.util.Wrappers;
import java.util.HashMap;
import javax.annotation.Nullable;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class MenuSourceRegistry {
   private static final HashMap<MenuType<?>, MenuSourceGetter<?>> MAP = new HashMap<>();

   public static synchronized <T extends AbstractContainerMenu> void register(MenuType<T> type, MenuSourceGetter<T> getter) {
      if (MAP.containsKey(type)) {
         MenuSourceGetter<T> prev = get(type);
         assert prev != null;
         MenuSourceGetter<T> next = MenuSourceGetter.compound(prev, getter);
         MAP.put(type, next);
      } else {
         MAP.put(type, getter);
      }
   }

   @Nullable
   public static <T extends AbstractContainerMenu> MenuSourceGetter<T> get(MenuType<T> type) {
      return !MAP.containsKey(type) ? null : (MenuSourceGetter)Wrappers.cast(MAP.get(type));
   }
}

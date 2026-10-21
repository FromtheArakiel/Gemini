package dev.xkmc.l2core.base.tile;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;

@ParametersAreNonnullByDefault
public interface BaseContainerListener extends ContainerListener {
   void notifyTile();

   default void containerChanged(Container cont) {
      this.notifyTile();
   }
}

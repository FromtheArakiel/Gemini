package dev.xkmc.l2menustacker.click.writable;

import net.minecraft.world.Container;

public record RealContainerCallback(Container container) implements ContainerCallback {
   @Override
   public void update() {
      this.container.setChanged();
   }
}

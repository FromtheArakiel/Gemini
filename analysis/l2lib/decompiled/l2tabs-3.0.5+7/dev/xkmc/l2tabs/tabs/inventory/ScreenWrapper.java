package dev.xkmc.l2tabs.tabs.inventory;

import dev.xkmc.l2tabs.tabs.core.ITabScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public record ScreenWrapper(int x, int y, int w, int h, Screen screen) implements ITabScreen {
   public static ITabScreen of(Screen screen) {
      if (screen instanceof AbstractContainerScreen<?> tx) {
         return new ScreenWrapper(tx.getGuiLeft(), tx.getGuiTop(), tx.getXSize(), tx.getYSize(), screen);
      } else {
         int x = (screen.width - 176) / 2;
         int y = (screen.height - 166) / 2;
         return new ScreenWrapper(x, y, 176, 166, screen);
      }
   }

   @Override
   public int getGuiLeft() {
      return this.x;
   }

   @Override
   public int getGuiTop() {
      return this.y;
   }

   @Override
   public int getXSize() {
      return this.w;
   }

   @Override
   public int getYSize() {
      return this.h;
   }

   @Override
   public Screen asScreen() {
      return this.screen;
   }
}

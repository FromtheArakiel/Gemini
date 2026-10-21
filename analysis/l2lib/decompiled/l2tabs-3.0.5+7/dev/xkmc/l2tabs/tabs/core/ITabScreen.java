package dev.xkmc.l2tabs.tabs.core;

import net.minecraft.client.gui.screens.Screen;

public interface ITabScreen {
   int getGuiLeft();

   int getGuiTop();

   default int screenWidth() {
      return this.asScreen().width;
   }

   default int screenHeight() {
      return this.asScreen().height;
   }

   int getXSize();

   int getYSize();

   default Screen asScreen() {
      return (Screen)this;
   }
}

package dev.xkmc.l2tabs.tabs.core;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class PageFlipButtons {
   public static Button getLeftButton(ITabScreen screen, OnPress o) {
      int radius = 3;
      return new FloatingButton(screen::getGuiLeft, screen::getGuiTop, radius, -26 + radius, 26 - radius * 2, 26 - radius * 2, Component.literal("<"), o);
   }

   public static Button getRightButton(int max, ITabScreen screen, OnPress o) {
      int radius = 3;
      return new FloatingButton(
         screen::getGuiLeft, screen::getGuiTop, (max - 1) * 26 + radius, -26 + radius, 26 - radius * 2, 26 - radius * 2, Component.literal(">"), o
      );
   }
}

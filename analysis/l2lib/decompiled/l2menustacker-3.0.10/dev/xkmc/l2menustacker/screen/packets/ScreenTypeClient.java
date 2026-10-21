package dev.xkmc.l2menustacker.screen.packets;

import dev.xkmc.l2core.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class ScreenTypeClient {
   public static void none() {
      Minecraft.getInstance().setScreen(null);
   }

   public static void player() {
      Minecraft.getInstance().setScreen(new InventoryScreen(Proxy.getClientPlayer()));
   }
}

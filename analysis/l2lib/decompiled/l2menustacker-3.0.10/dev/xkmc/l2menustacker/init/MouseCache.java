package dev.xkmc.l2menustacker.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

public class MouseCache {
   private static boolean grab;
   private static double mx;
   private static double my;

   public static void cacheMousePos() {
      MouseHandler mouse = Minecraft.getInstance().mouseHandler;
      mx = mouse.xpos();
      my = mouse.ypos();
      grab = true;
   }

   public static void onReleaseMouse() {
      if (grab) {
         grab = false;
         InputConstants.grabOrReleaseMouse(Minecraft.getInstance().getWindow().getWindow(), 212993, mx, my);
         MouseHandler mouse = Minecraft.getInstance().mouseHandler;
         mouse.xpos = mx;
         mouse.ypos = my;
      }
   }
}

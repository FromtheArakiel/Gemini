package dev.xkmc.l2itemselector.init.data;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;

public enum L2Keys {
   UP("key.l2mods.up", "Up", 265),
   DOWN("key.l2mods.down", "Down", 264),
   LEFT("key.l2mods.left", "Left", 263),
   RIGHT("key.l2mods.right", "Right", 262),
   SWAP("key.l2mods.swap", "Swap", 82),
   SHIFT("key.l2mods.shift", "Hold Selection", 340),
   ALT("key.l2mods.alt", "Alternative Selector", 342);

   public final String id;
   public final String def;
   public final int key;
   public final KeyMapping map;

   public static boolean hasShiftDown() {
      return SHIFT.map.isDown();
   }

   public static boolean hasAltDown() {
      return ALT.map.isDown();
   }

   public static boolean hasCtrlDown() {
      return Screen.hasControlDown();
   }

   L2Keys(String id, String def, int key) {
      this.id = id;
      this.def = def;
      this.key = key;
      this.map = new KeyMapping(id, key, "key.categories.l2mods");
   }
}

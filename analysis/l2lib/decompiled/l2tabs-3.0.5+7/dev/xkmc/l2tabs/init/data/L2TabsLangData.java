package dev.xkmc.l2tabs.init.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum L2TabsLangData {
   INVENTORY("menu.tabs.inventory", "Inventory", 0),
   ATTRIBUTE("menu.tabs.attribute", "Attributes", 0),
   CURIOS("menu.tabs.curios", "Curios", 0),
   DETAIL("menu.tabs.attribute.detail", "Press Shift to show details", 0),
   BASE("menu.tabs.attribute.base", "Base value: %s", 1),
   ADD("menu.tabs.attribute.add", "Addition: %s", 1),
   MULT_BASE("menu.tabs.attribute.mult_base", "Multiply Base: %s", 1),
   MULT_TOTAL("menu.tabs.attribute.mult_all", "Multiply total: %s", 1),
   FORMAT("menu.tabs.attribute.format", "(%s%s)x(1%s)x%s=%s", 5),
   INTRINSIC("menu.tabs.attribute.intrinsic", "Intrinsic Value: %s", 1);

   private final String key;
   private final String def;
   private final int arg;

   L2TabsLangData(String key, String def, int arg) {
      this.key = key;
      this.def = def;
      this.arg = arg;
   }

   public MutableComponent get(Object... args) {
      if (args.length != this.arg) {
         throw new IllegalArgumentException("for " + this.name() + ": expect " + this.arg + " parameters, got " + args.length);
      } else {
         return Component.translatable(this.key, args);
      }
   }

   public static void genLang(RegistrateLangProvider pvd) {
      for (L2TabsLangData lang : values()) {
         pvd.add(lang.key, lang.def);
      }
   }
}

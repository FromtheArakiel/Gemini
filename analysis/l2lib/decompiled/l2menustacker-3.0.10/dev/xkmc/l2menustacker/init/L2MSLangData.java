package dev.xkmc.l2menustacker.init;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum L2MSLangData {
   QUICK_ACCESS("tooltip.quick_access", "Right click in GUI to open", 0);

   private final String key;
   private final String def;
   private final int arg;

   L2MSLangData(String key, String def, int arg) {
      this.key = "l2menustacker." + key;
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
      for (L2MSLangData lang : values()) {
         pvd.add(lang.key, lang.def);
      }
   }
}

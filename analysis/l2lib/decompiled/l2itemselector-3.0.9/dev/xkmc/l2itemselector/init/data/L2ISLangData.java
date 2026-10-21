package dev.xkmc.l2itemselector.init.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum L2ISLangData {
   private final String key;
   private final String def;
   private final int arg;

   L2ISLangData(String key, String def, int arg) {
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
      for (L2ISLangData lang : values()) {
         pvd.add(lang.key, lang.def);
      }

      for (L2Keys lang : L2Keys.values()) {
         pvd.add(lang.id, lang.def);
      }

      pvd.add("key.categories.l2mods", "Keybinds for L2 Mods");
   }
}

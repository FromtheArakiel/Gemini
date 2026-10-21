package dev.xkmc.l2core.util;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2serial.util.ModContainerHack;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;

public class ConfigInit {
   private static final ConcurrentHashMap<String, ConfigInit> MAP = new ConcurrentHashMap<>();
   private String folder = null;
   private String path = "";
   private Type type;
   private IConfigSpec spec;

   @Nullable
   public static ConfigInit get(String path) {
      return MAP.get(path);
   }

   public Type getType() {
      return this.type;
   }

   public IConfigSpec getSpec() {
      return this.spec;
   }

   public String getPath() {
      return this.path;
   }

   protected void folder(String str) {
      if (this.folder != null) {
         throw new IllegalStateException("folder already set to " + this.folder);
      }

      this.folder = str;
   }

   protected void markL2() {
      this.folder("l2configs/");
   }

   protected void markPlain() {
      this.folder("");
   }

   public static <T extends ConfigInit> T register(L2Registrate reg, Type type, Function<ConfigInit.Builder, T> factory) {
      ConfigInit.Builder builder = new ConfigInit.Builder(reg);
      T ans = (T)factory.apply(builder);
      ModConfigSpec spec = builder.build();
      register(reg, type, spec, ans);
      return ans;
   }

   private static void register(L2Registrate reg, Type type, IConfigSpec spec, ConfigInit val) {
      if (val.folder == null) {
         throw new IllegalStateException("Config must specify folder");
      }

      ModContainer mod = ModContainerHack.getMod(reg.getModid());
      String path = val.folder + mod.getModId() + "-" + type.extension() + ".toml";
      mod.registerConfig(type, spec, path);
      val.path = path;
      val.type = type;
      val.spec = spec;
      MAP.put(path, val);
      RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> initClient(mod));
      reg.initConfigTitle(mod);
      String typeName = RegistrateLangProvider.toEnglishName(type.extension());
      String fileName = reg.getModid() + ".configuration.section." + path.replaceAll("[-_/]", ".");
      String title = mod.getModInfo().getDisplayName() + " " + typeName + " Configuration";
      reg.addRawLang(fileName, title);
      reg.addRawLang(fileName + ".title", title);
   }

   private static void initClient(ModContainer mod) {
      mod.registerExtensionPoint(IConfigScreenFactory.class, () -> ConfigurationScreen::new);
   }

   public static class Builder extends net.neoforged.neoforge.common.ModConfigSpec.Builder {
      private final L2Registrate reg;
      private String text = null;

      Builder(L2Registrate reg) {
         this.reg = reg;
      }

      @Deprecated
      public ConfigInit.Builder push(String path) {
         super.push(path);
         return this;
      }

      public ConfigInit.Builder push(String path, String name) {
         this.reg.addRawLang(this.reg.getModid() + ".configuration." + path, name);
         super.push(path);
         return this;
      }

      public ConfigInit.Builder text(String text) {
         this.text = text;
         return this;
      }

      public <T> ConfigValue<T> define(List<String> path, ValueSpec value, Supplier<T> defaultSupplier) {
         if (this.text == null) {
            throw new IllegalStateException("text not specified");
         }

         this.reg.addRawLang(this.reg.getModid() + ".configuration." + path.getLast(), this.text);
         String comment = value.getComment();
         this.reg.addRawLang(this.reg.getModid() + ".configuration." + path.getLast() + ".tooltip", comment == null ? "" : comment);
         return super.define(path, value, defaultSupplier);
      }

      public net.neoforged.neoforge.common.ModConfigSpec.Builder comment(String comment) {
         return super.comment(comment);
      }
   }
}

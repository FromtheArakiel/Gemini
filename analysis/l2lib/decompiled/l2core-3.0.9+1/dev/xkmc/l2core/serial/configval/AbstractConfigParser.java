package dev.xkmc.l2core.serial.configval;

import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2serial.util.Wrappers;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class AbstractConfigParser {
   private static Map<String, ModConfig> getMap() {
      try {
         Field field = ConfigTracker.class.getDeclaredField("fileMap");
         field.setAccessible(true);
         return (Map<String, ModConfig>)Wrappers.cast(field.get(ConfigTracker.INSTANCE));
      } catch (Exception e) {
         L2Core.LOGGER.throwing(e);
         return Map.of();
      }
   }

   @Nullable
   private static IConfigSpec getSpec(String path) {
      ConfigInit init = ConfigInit.get(path);
      if (init != null) {
         return init.getSpec();
      } else {
         Map<String, ModConfig> map = getMap();
         ModConfig file = map.get(path);
         if (file == null) {
            L2Core.LOGGER.warn("File {} is not a config file", path);
            return null;
         } else {
            return file.getSpec();
         }
      }
   }

   public static Optional<Object> parse(String path, List<String> line) {
      if (getSpec(path) instanceof ModConfigSpec modSpec && modSpec.isLoaded()) {
         return Optional.ofNullable((ConfigValue)modSpec.getValues().get(line)).map(ConfigValue::get);
      } else {
         L2Core.LOGGER.warn("File {} is not a loaded config file", path);
         return Optional.empty();
      }
   }
}

package dev.arakiel.gemini.component.internal.base;

import dev.arakiel.gemini.component.ComponentRegistry;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentLoadingException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;
import javax.annotation.Nonnull;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ComponentsInternals {
   public static final Logger LOGGER = LogManager.getLogger("Gemini Components");
   private static final int DEFAULT_MAX_WARNINGS_PER_COMPONENT = 5;
   private static final boolean DEFAULT_LOG_DESERIALIZATION_WARNINGS = true;
   private static final int configVersion = 2;
   private static boolean logDeserializationWarnings = true;
   private static int maxWarningsPerComponent = 5;
   private static final Object2IntMap<String> warningCounts = new Object2IntOpenHashMap();

   public static void init() {
      Path path = FabricLoader.getInstance().getConfigDir().resolve("gemini.properties");

      try (Reader reader = Files.newBufferedReader(path)) {
         Properties cfg = new Properties();
         cfg.load(reader);
         if (Integer.parseInt(cfg.getProperty("config-version")) < 2) {
            writeConfigFile(path);
            cfg.clear();
         }

         logDeserializationWarnings = Boolean.parseBoolean(cfg.getProperty("log-deserialization-warnings", String.valueOf(true)));
         maxWarningsPerComponent = Integer.parseInt(cfg.getProperty("max-deserialization-warnings", String.valueOf(5)));
      } catch (IOException var6) {
         writeConfigFile(path);
      }
   }

   private static void writeConfigFile(Path path) {
      try {
         Files.writeString(
            path,
            "# If set to false, warnings will not get logged when a component fails to be resolved (typically due to mods being removed)\n# Default value: %1$s\nlog-deserialization-warnings = %1$s\n# If log-deserialization-warnings is enabled, warnings will be printed at most *this number of times* for every component type\n# Default value: %2$d\nmax-deserialization-warnings = %2$d\n\n# Internal value, do not edit or your changes may be arbitrarily reset\nconfig-version = %3$d\n"
               .formatted(true, 5, 2)
         );
      } catch (IOException var2) {
         LOGGER.error("Failed to write config file at {}", path);
      }
   }

   @Nonnull
   public static <R> R createFactory(Class<R> factoryClass) {
      try {
         return (R)(Object)MethodHandles.lookup().findConstructor(factoryClass, MethodType.methodType(void.class)).invoke();
      } catch (Throwable var2) {
         throw new StaticComponentLoadingException("Failed to instantiate generated component factory", var2);
      }
   }

   public static void logDeserializationWarnings(Collection<String> missedKeyIds) {
      if (logDeserializationWarnings) {
         for (String missedKeyId : missedKeyIds) {
            int warningCount = warningCounts.getInt(missedKeyId);
            if (warningCount < maxWarningsPerComponent) {
               ResourceLocation id = ResourceLocation.tryParse(missedKeyId);
               String cause;
               if (id == null) {
                  cause = "invalid identifier";
               } else if (ComponentRegistry.get(id) == null) {
                  cause = "unregistered key";
               } else {
                  cause = "provider does not have ";
               }

               LOGGER.warn(
                  "Failed to deserialize component: {} {}{}",
                  cause,
                  missedKeyId,
                  warningCount + 1 >= maxWarningsPerComponent ? " (last warning for this component)" : ""
               );
               warningCounts.put(missedKeyId, warningCount + 1);
            }
         }
      }
   }

   @NotNull
   public static String getClientOptionalModAdvice() {
      return FabricLoader.getInstance().isDevelopmentEnvironment()
         ? "\n§eDEV ADVICE: If your mod is supposed to be client-optional, try overriding isRequiredOnClient() in your component."
         : "";
   }
}

package dev.xkmc.l2core.serial.config;

import com.google.gson.JsonElement;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

public abstract class ConfigDataProvider implements DataProvider {
   private final DataGenerator generator;
   private final CompletableFuture<Provider> pvd;
   private final String name;
   private final Map<String, ConfigDataProvider.ConfigEntry<?>> map = new HashMap<>();

   public ConfigDataProvider(DataGenerator generator, CompletableFuture<Provider> pvd, String name) {
      this.generator = generator;
      this.pvd = pvd;
      this.name = name;
   }

   public abstract void add(ConfigDataProvider.Collector var1);

   public CompletableFuture<?> run(CachedOutput cache) {
      return this.pvd.thenCompose(lookup -> {
         Path folder = this.generator.getPackOutput().getOutputFolder();
         this.add(new ConfigDataProvider.Collector(this.map));
         List<CompletableFuture<?>> list = new ArrayList<>();
         this.map.forEach((k, v) -> {
            JsonElement elem = v.serialize(lookup);
            if (elem != null) {
               Path path = folder.resolve(k + ".json");
               list.add(DataProvider.saveStable(cache, elem, path));
            }
         });
         return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
      });
   }

   public String getName() {
      return this.name;
   }

   public record Collector(Map<String, ConfigDataProvider.ConfigEntry<?>> map) {
      public <T extends BaseConfig> void add(ConfigTypeEntry<T> type, ResourceLocation id, T config) {
         this.map.put(type.asPath(id), new ConfigDataProvider.ConfigEntry<>(type, id, config));
      }
   }

   public record ConfigEntry<T extends BaseConfig>(ConfigTypeEntry<T> type, ResourceLocation id, T config) {
      @Nullable
      public JsonElement serialize(Provider pvd) {
         return new JsonCodec(pvd).toJson(this.config, this.type.cls());
      }
   }
}

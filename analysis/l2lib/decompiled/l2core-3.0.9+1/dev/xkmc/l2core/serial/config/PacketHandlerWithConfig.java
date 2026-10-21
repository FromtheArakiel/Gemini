package dev.xkmc.l2core.serial.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2serial.network.PacketHandler;
import dev.xkmc.l2serial.network.PacketHandler.PacketConfiguration;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

public class PacketHandlerWithConfig extends PacketHandler {
   static final Map<String, PacketHandlerWithConfig> INTERNAL = new ConcurrentHashMap<>();
   public ArrayList<PacketHandlerWithConfig.ConfigInstance> configs = new ArrayList<>();
   public final String config_path;
   final PacketHandlerWithConfig.ConfigReloadListener listener;
   final List<Runnable> listener_before = new ArrayList<>();
   final List<Runnable> listener_after = new ArrayList<>();
   final List<Runnable> client_listener_before = new ArrayList<>();
   final List<Runnable> client_listener_after = new ArrayList<>();
   final Map<String, BaseConfigType<?>> types = new HashMap<>();

   public static void onDatapackSync(OnDatapackSyncEvent event) {
      for (PacketHandlerWithConfig handler : INTERNAL.values()) {
         SyncPacket packet = new SyncPacket(handler.modid, handler.configs);
         if (event.getPlayer() == null) {
            L2Core.PACKET_HANDLER.toAllClient(packet);
         } else {
            L2Core.PACKET_HANDLER.toClientPlayer(packet, event.getPlayer());
         }
      }
   }

   public static void addReloadListeners(AddReloadListenerEvent event) {
      for (PacketHandlerWithConfig handler : INTERNAL.values()) {
         if (handler.listener != null) {
            event.addListener(handler.listener);
         }
      }
   }

   @SafeVarargs
   public PacketHandlerWithConfig(String id, int version, Function<PacketHandler, PacketConfiguration<?>>... values) {
      super(id, version, values);
      INTERNAL.put(id, this);
      this.config_path = id + "_config";
      this.listener = new PacketHandlerWithConfig.ConfigReloadListener(this.config_path);
      this.listener_before.add(this.configs::clear);
   }

   public void addBeforeReloadListener(Runnable runnable) {
      this.listener_before.add(runnable);
   }

   public void addAfterReloadListener(Runnable runnable) {
      this.listener_after.add(runnable);
   }

   public void addClientBeforeReloadListener(Runnable runnable) {
      this.client_listener_before.add(runnable);
   }

   public void addClientAfterReloadListener(Runnable runnable) {
      this.client_listener_after.add(runnable);
   }

   public <T extends BaseConfig> void addConfig(String id, Class<T> loader) {
      BaseConfigType<T> c = new BaseConfigType<>(this, id, loader);
      this.types.put(id, c);
      this.addBeforeReloadListener(c::beforeReload);
      this.addAfterReloadListener(c::afterReload);
   }

   public <T extends BaseConfig> void addCachedConfig(String id, Class<T> loader) {
      MergedConfigType<T> c = new MergedConfigType<>(this, id, loader);
      this.types.put(id, c);
      this.addBeforeReloadListener(c::beforeReload);
      this.addAfterReloadListener(c::afterReload);
   }

   <T extends BaseConfig> T getCachedConfig(String id) {
      MergedConfigType<T> type = (MergedConfigType<T>)Wrappers.cast(this.types.get(id));
      return type.load();
   }

   public record ConfigInstance(String name, ResourceLocation id, BaseConfig config) {
   }

   class ConfigReloadListener extends SimpleJsonResourceReloadListener {
      public ConfigReloadListener(String path) {
         super(new Gson(), path);
      }

      protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler) {
         PacketHandlerWithConfig.this.listener_before.forEach(Runnable::run);
         map.forEach((k, v) -> {
            if (k.getNamespace().startsWith("_") || ModList.get().isLoaded(k.getNamespace())) {
               String id = k.getPath().split("/")[0];
               if (PacketHandlerWithConfig.this.types.containsKey(id)) {
                  String name = k.getPath().substring(id.length() + 1);
                  ResourceLocation nk = k.withPath(name);
                  this.addJson(PacketHandlerWithConfig.this.types.get(id), nk, v);
               }
            }
         });
         PacketHandlerWithConfig.this.listener_after.forEach(Runnable::run);
      }

      private <T extends BaseConfig> void addJson(BaseConfigType<T> type, ResourceLocation k, JsonElement v) {
         T config = (T)new JsonCodec(this.getRegistryLookup()).from(v, type.cls, null);
         if (config != null) {
            this.addServerConfig(type, k, config);
         }
      }

      private <T extends BaseConfig> void addServerConfig(BaseConfigType<T> type, ResourceLocation k, T config) {
         config.id = k;
         type.configs.put(k, config);
         PacketHandlerWithConfig.this.configs.add(new PacketHandlerWithConfig.ConfigInstance(type.id, k, config));
      }

      private <T extends BaseConfig> void addClientConfig(BaseConfigType<T> type, ResourceLocation k, T config) {
         config.id = k;
         type.clientConfigs.put(k, config);
      }

      public void apply(ArrayList<PacketHandlerWithConfig.ConfigInstance> list) {
         PacketHandlerWithConfig.this.client_listener_before.forEach(Runnable::run);
         PacketHandlerWithConfig.this.types.values().forEach(BaseConfigType::clientBeforeReload);

         for (PacketHandlerWithConfig.ConfigInstance e : list) {
            this.addClientConfig(PacketHandlerWithConfig.this.types.get(e.name), e.id(), (BaseConfig)Wrappers.cast(e.config));
         }

         PacketHandlerWithConfig.this.types.values().forEach(BaseConfigType::clientAfterReload);
         PacketHandlerWithConfig.this.client_listener_after.forEach(Runnable::run);
      }
   }
}

package dev.xkmc.l2core.serial.config;

import dev.xkmc.l2core.util.ServerProxy;
import net.minecraft.resources.ResourceLocation;

public class MergedConfigType<T extends BaseConfig> extends BaseConfigType<T> {
   private T result;
   private T clientResult;

   MergedConfigType(PacketHandlerWithConfig parent, String id, Class<T> cls) {
      super(parent, id, cls);
   }

   T load() {
      return ServerProxy.isOnClient() ? this.loadClient() : this.loadServer();
   }

   T loadServer() {
      if (this.result != null) {
         return this.result;
      }

      this.result = new ConfigMerger<>(this.cls).apply(this.configs.values());
      this.result.id = ResourceLocation.fromNamespaceAndPath(this.parent.modid, this.id);
      return this.result;
   }

   T loadClient() {
      if (this.clientResult != null) {
         return this.clientResult;
      }

      this.clientResult = new ConfigMerger<>(this.cls).apply(this.clientConfigs.values());
      this.clientResult.id = ResourceLocation.fromNamespaceAndPath(this.parent.modid, this.id);
      return this.clientResult;
   }

   @Override
   public void afterReload() {
      this.result = null;
      if (this.cls.isAnnotationPresent(ConfigLoadOnStart.class)) {
         this.loadServer();
      }
   }

   @Override
   public void clientAfterReload() {
      this.clientResult = null;
      if (this.cls.isAnnotationPresent(ConfigLoadOnStart.class)) {
         this.loadClient();
      }
   }
}

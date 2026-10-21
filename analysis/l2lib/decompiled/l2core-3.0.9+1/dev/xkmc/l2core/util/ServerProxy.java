package dev.xkmc.l2core.util;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ServerProxy {
   @Nullable
   public static RegistryAccess getRegistryAccess() {
      if (FMLEnvironment.dist == Dist.CLIENT) {
         Level level = Proxy.getLevel();
         if (level != null) {
            return level.registryAccess();
         }
      }

      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      return server != null ? server.registryAccess() : null;
   }

   public static Optional<MinecraftServer> getServer() {
      return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
   }

   public static boolean isOnClient() {
      return FMLEnvironment.dist == Dist.DEDICATED_SERVER ? false : Proxy.isOnClient();
   }

   public static boolean hasClientLevel() {
      return FMLEnvironment.dist == Dist.DEDICATED_SERVER ? false : Proxy.isOnClient() && Proxy.getLevel() != null;
   }
}

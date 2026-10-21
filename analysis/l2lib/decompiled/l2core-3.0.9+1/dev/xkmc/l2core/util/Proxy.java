package dev.xkmc.l2core.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class Proxy {
   @Nullable
   public static Player getPlayer() {
      return FMLEnvironment.dist == Dist.CLIENT ? Minecraft.getInstance().player : null;
   }

   @Nullable
   public static Level getLevel() {
      if (FMLEnvironment.dist == Dist.CLIENT) {
         return Minecraft.getInstance().level;
      }

      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      return server != null ? server.overworld() : null;
   }

   @Deprecated
   public static Optional<MinecraftServer> getServer() {
      return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
   }

   @Nullable
   public static LocalPlayer getClientPlayer() {
      return Minecraft.getInstance().player;
   }

   static boolean isOnClient() {
      return getServer().isEmpty() || RenderSystem.isOnRenderThread();
   }
}

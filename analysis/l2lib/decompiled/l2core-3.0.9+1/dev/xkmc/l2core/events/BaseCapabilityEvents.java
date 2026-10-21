package dev.xkmc.l2core.events;

import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StartTracking;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber(modid = "l2core", bus = Bus.GAME)
public class BaseCapabilityEvents {
   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity().isAlive()) {
         for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
            holder.getOrCreate(event.getEntity()).tick(event.getEntity());
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.LOW)
   public static void onPlayerClone(Clone event) {
      for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
         ServerPlayer e = (ServerPlayer)event.getEntity();
         holder.getOrCreate(e).onClone(e, event.isWasDeath());
      }
   }

   @SubscribeEvent(priority = EventPriority.LOW)
   public static void onPlayerClone(PlayerRespawnEvent event) {
      for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
         ServerPlayer e = (ServerPlayer)event.getEntity();
         holder.network.toClient(e);
      }
   }

   @SubscribeEvent(priority = EventPriority.LOW)
   public static void onPlayerJoinLevel(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof ServerPlayer e) {
         for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
            holder.network.toClient(e);
            holder.network.toTracking(e);
         }
      }
   }

   @SubscribeEvent
   public static void onServerPlayerJoin(PlayerLoggedInEvent event) {
      ServerPlayer e = (ServerPlayer)event.getEntity();
      if (e != null) {
         for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
            holder.getOrCreate(e).init(e);
            holder.network.toClient(e);
            holder.network.toTracking(e);
         }
      }
   }

   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
         if (event.getTarget() instanceof ServerPlayer e) {
            holder.network.startTracking((ServerPlayer)event.getEntity(), e);
         }
      }
   }
}

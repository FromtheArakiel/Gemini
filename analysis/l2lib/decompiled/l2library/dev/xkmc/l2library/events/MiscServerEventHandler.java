package dev.xkmc.l2library.events;

import dev.xkmc.l2library.content.explosion.BaseExplosion;
import dev.xkmc.l2library.content.raytrace.RayTraceUtil;
import dev.xkmc.l2library.util.PlayerTracker;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent.Detonate;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;

@EventBusSubscriber(modid = "l2library", bus = Bus.GAME)
public class MiscServerEventHandler {
   @SubscribeEvent
   public static void serverTick(Post event) {
      RayTraceUtil.serverTick(event.getServer());
   }

   @SubscribeEvent
   public static void onPlayerTick(Pre event) {
      if (event.getEntity().tickCount < 5) {
         PlayerTracker.get(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onDetonate(Detonate event) {
      if (event.getExplosion() instanceof BaseExplosion exp) {
         event.getAffectedEntities().removeIf(e -> !exp.hurtEntity(e));
      }
   }

   @SubscribeEvent
   public static void onEntityStruck(EntityStruckByLightningEvent event) {
      if (event.getLightning().getTags().contains("l2library:safe_lightning")
         && (!(event.getEntity() instanceof LivingEntity) || event.getEntity() == event.getLightning().getCause())) {
         event.setCanceled(true);
      }
   }
}

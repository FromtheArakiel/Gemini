package dev.xkmc.l2library.events;

import dev.xkmc.l2library.content.raytrace.EntityTarget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2library", bus = Bus.GAME)
public class MiscClientEventHandler {
   @SubscribeEvent
   public static void clientTick(Post event) {
      for (EntityTarget target : EntityTarget.LIST) {
         target.tickRender();
      }
   }
}

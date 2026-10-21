package dev.xkmc.l2tabs.init;

import dev.xkmc.l2tabs.compat.api.TabCuriosCompat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2tabs", bus = Bus.MOD)
public class L2TabsClient {
   @SubscribeEvent
   public static void client(FMLClientSetupEvent event) {
      TabCuriosCompat.onClientInit();
   }
}

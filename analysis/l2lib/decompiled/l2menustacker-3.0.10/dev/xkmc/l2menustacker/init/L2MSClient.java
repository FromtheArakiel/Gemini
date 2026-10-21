package dev.xkmc.l2menustacker.init;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2menustacker", bus = Bus.MOD)
public class L2MSClient {
   @SubscribeEvent
   public static void client(FMLClientSetupEvent event) {
      event.enqueueWork(() -> {});
   }
}

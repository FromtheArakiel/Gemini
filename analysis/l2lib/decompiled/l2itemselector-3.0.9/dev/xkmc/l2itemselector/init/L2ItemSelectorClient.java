package dev.xkmc.l2itemselector.init;

import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.item.ItemSelectionOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2itemselector", bus = Bus.MOD)
public class L2ItemSelectorClient {
   @SubscribeEvent
   public static void registerOverlays(RegisterGuiLayersEvent event) {
      event.registerAbove(VanillaGuiLayers.CROSSHAIR, L2ItemSelector.loc("tool_select"), ItemSelectionOverlay.INSTANCE);
   }

   @SubscribeEvent
   public static void registerKeyMaps(RegisterKeyMappingsEvent event) {
      for (L2Keys e : L2Keys.values()) {
         event.register(e.map);
      }
   }
}

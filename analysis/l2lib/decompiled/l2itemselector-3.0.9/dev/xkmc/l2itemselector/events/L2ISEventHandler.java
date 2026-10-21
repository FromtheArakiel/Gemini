package dev.xkmc.l2itemselector.events;

import dev.xkmc.l2itemselector.select.item.ItemConvertor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre;

@EventBusSubscriber(modid = "l2itemselector", bus = Bus.GAME)
public class L2ISEventHandler {
   @SubscribeEvent
   public static void addItemToInventory(Pre event) {
      ItemStack prev = event.getItemEntity().getItem();
      ItemStack next = ItemConvertor.convert(prev, event.getPlayer());
      if (next != prev) {
         event.getItemEntity().setItem(next);
         event.setCanPickup(TriState.FALSE);
      }
   }
}

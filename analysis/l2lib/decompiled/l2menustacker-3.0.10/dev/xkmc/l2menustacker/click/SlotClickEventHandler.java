package dev.xkmc.l2menustacker.click;

import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2menustacker.init.L2MSLangData;
import dev.xkmc.l2menustacker.init.L2MSTagGen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonPressed.Pre;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2menustacker", bus = Bus.GAME)
public class SlotClickEventHandler {
   @SubscribeEvent
   public static void onScreenRightClick(Pre event) {
      if (event.getScreen() instanceof AbstractContainerScreen cont) {
         Slot slot = cont.getSlotUnderMouse();
         if (slot == null) {
            return;
         }

         if (slot.getItem().isStackable()) {
            ItemStack stack = slot.getItem();
            if (stack.getCount() > 1) {
               return;
            }

            if (!stack.is(L2MSTagGen.QUICK_ACCESS)) {
               return;
            }
         }

         if (event.getButton() == 1) {
            boolean b1 = slot.container == Proxy.getClientPlayer().getInventory();
            boolean b2 = cont.getMenu().containerId > 0;
            if (b1 || b2) {
               int inv = b1 ? slot.getSlotIndex() : -1;
               int ind = inv == -1 ? slot.index : -1;
               int wid = cont.getMenu().containerId;
               if (inv >= 0 || ind >= 0) {
                  for (SlotClickHandler handler : SlotClickHandler.MAP.values()) {
                     if (handler.isAllowed(slot.getItem())) {
                        handler.slotClickToServer(ind, inv, wid);
                        event.setCanceled(true);
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void tooltipEvent(ItemTooltipEvent event) {
      if (event.getItemStack().is(L2MSTagGen.QUICK_ACCESS_VANILLA)) {
         event.getToolTip().add(L2MSLangData.QUICK_ACCESS.get().withStyle(ChatFormatting.GRAY));
      }
   }
}

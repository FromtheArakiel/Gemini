package dev.xkmc.l2itemselector.events;

import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.init.data.L2Keys;
import dev.xkmc.l2itemselector.select.ISelectionListener;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent;
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Pre;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2itemselector", bus = Bus.GAME)
public class L2ISClientEventHandler {
   private static double scroll;

   @SubscribeEvent
   public static void inputEvent(GenericKeyEvent event) {
      LocalPlayer player = Proxy.getClientPlayer();
      if (player != null) {
         Optional<ISelectionListener> sel = SelectionRegistry.getClientActiveListener(player);
         if (!sel.isEmpty()) {
            for (L2Keys k : L2Keys.values()) {
               if (event.test(k.map.getKey()) && event.getAction() == 1) {
                  sel.get().handleClientKey(k, player);
                  return;
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void mouseEvent(Pre event) {
      NeoForge.EVENT_BUS.post(new GenericKeyEvent(e -> e.getType() == Type.MOUSE && e.getValue() == event.getButton(), event.getAction()));
   }

   @SubscribeEvent
   public static void keyEvent(Key event) {
      NeoForge.EVENT_BUS.post(new GenericKeyEvent(e -> e.getType() != Type.MOUSE && e.getValue() == event.getKey(), event.getAction()));
      LocalPlayer player = Proxy.getClientPlayer();
      if (player != null) {
         Optional<ISelectionListener> sel = SelectionRegistry.getClientActiveListener(player);
         if (!sel.isEmpty()) {
            for (int i = 0; i < 9; i++) {
               if (sel.get().handleClientNumericKey(i, Minecraft.getInstance().options.keyHotbarSlots[i]::consumeClick)) {
                  return;
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void scrollEvent(MouseScrollingEvent event) {
      double d0 = event.getScrollDeltaY();
      if (d0 != 0.0) {
         scroll += d0;
         int i = (int)scroll;
         scroll -= i;
         LocalPlayer player = Proxy.getClientPlayer();
         if (player != null) {
            Optional<ISelectionListener> sel = SelectionRegistry.getClientActiveListener(player);
            if (!sel.isEmpty()) {
               if (sel.get().scrollBypassShift() || !(Boolean)L2ISConfig.CLIENT.selectionScrollRequireShift.get() || sel.get().isHoldKeyDown(player)) {
                  if (sel.get().handleClientScroll(i, d0, player)) {
                     event.setCanceled(true);
                  }
               }
            }
         }
      }
   }
}

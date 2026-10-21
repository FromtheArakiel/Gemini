package dev.xkmc.l2tabs.tabs.contents;

import dev.xkmc.l2menustacker.init.MouseCache;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.l2tabs.tabs.core.TabBase;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.l2tabs.tabs.inventory.InvTabData;
import dev.xkmc.l2tabs.tabs.inventory.ScreenWrapper;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ScreenEvent.BackgroundRendered;
import net.neoforged.neoforge.client.event.ScreenEvent.Init.Post;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2tabs", bus = Bus.GAME)
public class TabInventory extends TabBase<InvTabData, TabInventory> {
   public static Predicate<Screen> inventoryTest = e -> e instanceof InventoryScreen;
   public static Runnable openInventory = () -> {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         Minecraft.getInstance().setScreen(new InventoryScreen(player));
      }
   };

   @SubscribeEvent
   public static void guiPostInit(Post event) {
      if (inventoryTest.test(event.getScreen())) {
         TabManager<InvTabData> manager = new TabManager<>(ScreenWrapper.of(event.getScreen()), new InvTabData());
         manager.init(event::addListener, (TabToken<InvTabData, ?>)L2Tabs.TAB_INVENTORY.get());
      }
   }

   public static void renderTabs(GuiGraphics g, Screen screen) {
      for (GuiEventListener e : screen.children()) {
         if (e instanceof TabBase<?, ?> tab && tab.manager.selected != tab.token) {
            tab.renderBackground(g);
         }
      }
   }

   @SubscribeEvent
   public static void guiPostRenderBG(BackgroundRendered event) {
      renderTabs(event.getGuiGraphics(), event.getScreen());
   }

   public TabInventory(int index, TabToken<InvTabData, TabInventory> token, TabManager<InvTabData> manager, Component title) {
      super(index, token, manager, title);
   }

   @Override
   public void onTabClicked() {
      MouseCache.cacheMousePos();
      openInventory.run();
   }
}

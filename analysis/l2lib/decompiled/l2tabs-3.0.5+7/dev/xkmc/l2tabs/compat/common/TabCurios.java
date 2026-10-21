package dev.xkmc.l2tabs.compat.common;

import dev.xkmc.l2menustacker.init.MouseCache;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.l2tabs.init.data.OpenCurioHandler;
import dev.xkmc.l2tabs.init.data.OpenCuriosPacket;
import dev.xkmc.l2tabs.tabs.core.TabBase;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.l2tabs.tabs.inventory.InvTabData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TabCurios extends TabBase<InvTabData, TabCurios> {
   private static final ResourceLocation ICON = L2Tabs.loc("curios");

   public TabCurios(int index, TabToken<InvTabData, TabCurios> token, TabManager<InvTabData> manager, Component title) {
      super(index, token, manager, title);
   }

   @Override
   public void onTabClicked() {
      MouseCache.cacheMousePos();
      L2Tabs.PACKET_HANDLER.toServer(new OpenCuriosPacket(OpenCurioHandler.CURIO_OPEN));
   }

   @Override
   public void renderBackground(GuiGraphics g) {
      if (this.getX() != 0 || this.getY() != 0) {
         if (this.visible) {
            this.token.draw(g, this.getX(), this.getY(), this.manager.selected == this.token, this.index);
            g.blitSprite(ICON, this.getX() + 6, this.getY() + 10, 14, 14);
         }
      }
   }
}

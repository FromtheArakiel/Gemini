package dev.xkmc.l2core.base.menu.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class BaseContainerScreen<T extends BaseContainerMenu<T>> extends AbstractContainerScreen<T> {
   public BaseContainerScreen(T cont, Inventory plInv, Component title) {
      super(cont, plInv, title);
      this.imageHeight = ((BaseContainerMenu)this.menu).getLayout().getHeight();
      this.inventoryLabelY = ((BaseContainerMenu)this.menu).getLayout().getPlInvY() - 11;
   }

   public void render(GuiGraphics g, int mx, int my, float partial) {
      super.render(g, mx, my, partial);
      this.renderTooltip(g, mx, my);
   }

   protected MenuLayoutConfig.ScreenRenderer getRenderer() {
      return ((BaseContainerMenu)this.menu).getLayout().getRenderer(((BaseContainerMenu)this.menu).getLayoutId(), this);
   }

   protected boolean click(int btn) {
      if (((BaseContainerMenu)this.menu).clickMenuButton(((BaseContainerMenu)this.menu).player, btn) && Minecraft.getInstance().gameMode != null) {
         Minecraft.getInstance().gameMode.handleInventoryButtonClick(((BaseContainerMenu)this.menu).containerId, btn);
         return true;
      } else {
         return false;
      }
   }
}

package dev.xkmc.l2tabs.tabs.core;

import dev.xkmc.l2tabs.init.L2Tabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class TabBase<G extends TabGroupData<G>, T extends TabBase<G, T>> extends FloatingButton {
   public final int index;
   public final TabToken<G, T> token;
   public final TabManager<G> manager;
   public int page;

   public TabBase(int index, TabToken<G, T> token, TabManager<G> manager, Component title) {
      super(token.getType().width, token.getType().height, title, b -> ((TabBase)b).onTabClicked());
      this.index = index;
      this.token = token;
      this.manager = manager;
   }

   public abstract void onTabClicked();

   public void onTooltip(GuiGraphics g, int x, int y) {
      g.renderTooltip(Minecraft.getInstance().font, this.getMessage(), x, y);
   }

   public void renderBackground(GuiGraphics g) {
      if (this.getX() != 0 || this.getY() != 0) {
         if (this.visible) {
            this.token.draw(g, this.getX(), this.getY(), this.manager.selected == this.token, this.index);
            this.renderIcon(g);
         }
      }
   }

   public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
      if (this.getX() != 0 || this.getY() != 0) {
         if (this.manager.selected == this.token) {
            this.renderBackground(g);
         }

         if (this == this.manager.list.getLast()) {
            this.manager.onToolTipRender(g, mouseX, mouseY);
         }
      }
   }

   protected void renderIcon(GuiGraphics g) {
      ItemStack stack = get(L2Tabs.TABS.reg().wrapAsHolder(this.token)).getDefaultInstance();
      if (!stack.isEmpty()) {
         this.token.getType().drawIcon(g, this.getX(), this.getY(), stack);
      }
   }

   private static Item get(Holder<TabToken<?, ?>> holder) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level == null) {
         return Items.AIR;
      }

      Item ans = (Item)L2Tabs.ICON.get(level.registryAccess(), holder);
      return ans == null ? Items.AIR : ans;
   }
}

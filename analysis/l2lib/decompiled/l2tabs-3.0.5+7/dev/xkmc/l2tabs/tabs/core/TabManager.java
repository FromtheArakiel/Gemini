package dev.xkmc.l2tabs.tabs.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class TabManager<G extends TabGroupData<G>> {
   protected final List<TabBase<G, ?>> list = new ArrayList<>();
   public final ITabScreen screen;
   public final G token;
   private Button left;
   private Button right;
   public int tabPage;
   public TabToken<G, ?> selected;
   private int maxPages = 0;

   public TabManager(ITabScreen screen, G token) {
      this.screen = screen;
      this.token = token;
   }

   public void init(Consumer<AbstractWidget> adder, TabToken<G, ?> selected) {
      if (this.token.shouldRender()) {
         TabGroup<G> group = this.token.getGroup();
         List<TabToken<G, ?>> token_list = new ArrayList<>(this.token.getTabs());
         token_list.removeIf(this.token::shouldHideTab);
         this.list.clear();
         this.selected = selected;
         int imgWidth = this.screen.getXSize();
         int imgHeight = this.screen.getYSize();
         int index = 0;
         int order = 0;
         int page = 0;

         for (TabToken<G, ?> token : token_list) {
            if (index > 0 && order == 0) {
               order++;
            }

            if (token == selected) {
               this.tabPage = page;
            }

            TabBase<G, ?> tab = token.create(order, this);
            tab.page = page;
            tab.setXRef(this.screen::getGuiLeft, group.type.getTabX(imgWidth, order));
            tab.setYRef(this.screen::getGuiTop, group.type.getTabY(imgHeight, order));
            this.list.add(tab);
            adder.accept(tab);
            if (++order == group.max() - 1) {
               order = 0;
               page++;
            }

            index++;
         }

         this.maxPages = order == 0 ? page : page + 1;
         adder.accept(this.left = PageFlipButtons.getLeftButton(this.screen, b -> {
            this.tabPage = Math.max(this.tabPage - 1, 0);
            this.updateVisibility();
         }));
         adder.accept(this.right = PageFlipButtons.getRightButton(group.max(), this.screen, b -> {
            this.tabPage = Math.min(this.tabPage + 1, this.maxPages);
            this.updateVisibility();
         }));
         this.updateVisibility();
      }
   }

   private void updateVisibility() {
      this.left.visible = this.left.active = this.tabPage > 0;
      this.right.visible = this.right.active = this.tabPage < this.maxPages - 1;

      for (TabBase<G, ?> tab : this.list) {
         tab.visible = tab.page == this.tabPage;
         tab.active = tab.visible;
      }
   }

   public Screen getScreen() {
      return this.screen.asScreen();
   }

   public void onToolTipRender(GuiGraphics stack, int mouseX, int mouseY) {
      for (TabBase<G, ?> tab : this.list) {
         if (tab.visible && tab.isHoveredOrFocused()) {
            tab.onTooltip(stack, mouseX, mouseY);
         }
      }
   }
}

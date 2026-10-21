package dev.xkmc.l2tabs.compat.common;

import dev.xkmc.l2core.base.menu.base.BaseContainerScreen;
import dev.xkmc.l2core.base.menu.base.MenuLayoutConfig.ScreenRenderer;
import dev.xkmc.l2tabs.compat.api.INamedSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class BaseCuriosListScreen<T extends BaseCuriosListMenu<T>> extends BaseContainerScreen<T> {
   public BaseCuriosListScreen(T cont, Inventory plInv, Component title) {
      super(cont, plInv, title);
   }

   protected void init() {
      super.init();
      if (this.topPos < 28) {
         this.topPos = 28;
      }

      int w = 10;
      int h = 11;
      int x = this.getGuiLeft() + this.titleLabelX + this.font.width(this.getTitle()) + 14;
      int y = this.getGuiTop() + 4;
      if (((BaseCuriosListMenu)this.menu).curios.page > 0) {
         this.addRenderableWidget(Button.builder(Component.literal("<"), e -> this.click(1)).pos(x - w - 1, y).size(w, h).build());
      }

      if (((BaseCuriosListMenu)this.menu).curios.page < ((BaseCuriosListMenu)this.menu).curios.total - 1) {
         this.addRenderableWidget(Button.builder(Component.literal(">"), e -> this.click(2)).pos(x, y).size(w, h).build());
      }
   }

   protected void renderBg(GuiGraphics g, float pTick, int mx, int my) {
      ScreenRenderer sr = this.getRenderer();
      sr.start(g);

      for (int i = 0; i < ((BaseCuriosListMenu)this.menu).curios.getRows() * 9; i++) {
         if (((BaseCuriosListMenu)this.menu).curios.getSlotAtPosition(i) != null) {
            sr.draw(g, "grid", "slot", i % 9 * 18 - 1, i / 9 * 18 - 1);
         }
      }
   }

   protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {
      super.renderLabels(p_281635_, p_282681_, p_283686_);
   }

   protected void renderTooltip(GuiGraphics g, int mx, int my) {
      LocalPlayer clientPlayer = Minecraft.getInstance().player;
      if (clientPlayer != null && clientPlayer.inventoryMenu.getCarried().isEmpty() && this.getSlotUnderMouse() != null) {
         Slot slot = this.getSlotUnderMouse();
         if (slot instanceof INamedSlot slotCurio && !slot.hasItem()) {
            g.renderTooltip(this.font, slotCurio.getName(), mx, my);
         }
      }

      super.renderTooltip(g, mx, my);
   }
}

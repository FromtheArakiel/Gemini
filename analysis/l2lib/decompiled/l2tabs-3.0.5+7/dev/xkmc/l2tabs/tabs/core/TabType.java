package dev.xkmc.l2tabs.tabs.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public enum TabType {
   ABOVE(DefaultTabs.UP),
   BELOW(DefaultTabs.BOTTOM),
   LEFT(DefaultTabs.LEFT),
   RIGHT(DefaultTabs.RIGHT);

   public final int width;
   public final int height;
   public final TabSprites sprite;

   TabType(TabSprites sprite) {
      this.width = sprite.w();
      this.height = sprite.h();
      this.sprite = sprite;
   }

   public void draw(TabSprites tex, GuiGraphics g, int x, int y, boolean selected, int index) {
      g.blitSprite(tex.get(index, selected), x, y, tex.w(), tex.h());
   }

   public void drawIcon(GuiGraphics g, int x, int y, ItemStack stack) {
      int i = x;
      int j = y;
      switch (this) {
         case ABOVE:
            i += 6;
            j += 9;
            break;
         case BELOW:
            i += 6;
            j += 6;
            break;
         case LEFT:
            i += 10;
            j += 5;
            break;
         case RIGHT:
            i += 6;
            j += 5;
      }

      g.renderFakeItem(stack, i, j);
      g.renderItemDecorations(Minecraft.getInstance().font, stack, i, j);
   }

   public int getX(int pIndex) {
      return switch (this) {
         case ABOVE, BELOW -> this.width * pIndex;
         case LEFT -> -this.width + 4;
         case RIGHT -> -4;
      };
   }

   public int getY(int pIndex) {
      return switch (this) {
         case ABOVE -> -this.height + 4;
         case BELOW -> -4;
         case LEFT, RIGHT -> this.height * pIndex;
      };
   }

   public boolean isMouseOver(int gx, int gy, int ind, double mx, double my) {
      int x = gx + this.getX(ind);
      int y = gy + this.getY(ind);
      return mx > x && mx < x + this.width && my > y && my < y + this.height;
   }

   public int getTabX(int imgWidth, int index) {
      return (this == RIGHT ? imgWidth : 0) + this.getX(index);
   }

   public int getTabY(int imgHeight, int index) {
      return (this == BELOW ? imgHeight : 0) + this.getY(index);
   }
}

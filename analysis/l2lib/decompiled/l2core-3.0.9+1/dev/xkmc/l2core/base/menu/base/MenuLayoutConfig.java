package dev.xkmc.l2core.base.menu.base;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.HashMap;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@SerialClass
public record MenuLayoutConfig(int height, HashMap<String, MenuLayoutConfig.Rect> side, HashMap<String, MenuLayoutConfig.Rect> comp) {
   public static ResourceLocation getTexture(ResourceLocation id) {
      return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "textures/gui/container/" + id.getPath() + ".png");
   }

   public MenuLayoutConfig.Rect getComp(String key) {
      return this.comp.getOrDefault(key, MenuLayoutConfig.Rect.ZERO);
   }

   public int getHeight() {
      return this.height;
   }

   public int getPlInvX() {
      return 8;
   }

   public int getPlInvY() {
      return this.height - 82;
   }

   @OnlyIn(Dist.CLIENT)
   public MenuLayoutConfig.ScreenRenderer getRenderer(ResourceLocation id, AbstractContainerScreen<?> gui) {
      return new MenuLayoutConfig.ScreenRenderer(id, gui);
   }

   @OnlyIn(Dist.CLIENT)
   public MenuLayoutConfig.ScreenRenderer getRenderer(ResourceLocation id, Screen gui, int x, int y, int w, int h) {
      return new MenuLayoutConfig.ScreenRenderer(id, gui, x, y, w, h);
   }

   public MenuLayoutConfig.Rect getSide(String key) {
      return this.side.getOrDefault(key, MenuLayoutConfig.Rect.ZERO);
   }

   public <T extends Slot> void getSlot(String key, MenuLayoutConfig.SlotFactory<T> fac, MenuLayoutConfig.SlotAcceptor con) {
      MenuLayoutConfig.Rect c = this.getComp(key);

      for (int j = 0; j < c.ry; j++) {
         for (int i = 0; i < c.rx; i++) {
            T slot = fac.getSlot(c.x + i * c.w, c.y + j * c.h);
            if (slot != null) {
               con.addSlot(key, i, j, slot);
            }
         }
      }
   }

   public int getWidth() {
      return 176;
   }

   public boolean within(String key, double x, double y) {
      MenuLayoutConfig.Rect c = this.getComp(key);
      return x > c.x && x < c.x + c.w && y > c.y && y < c.y + c.h;
   }

   @SerialClass
   public static class Rect {
      public static final MenuLayoutConfig.Rect ZERO = new MenuLayoutConfig.Rect();
      @SerialField
      public int x;
      @SerialField
      public int y;
      @SerialField
      public int w;
      @SerialField
      public int h;
      @SerialField
      public int rx = 1;
      @SerialField
      public int ry = 1;
   }

   @OnlyIn(Dist.CLIENT)
   public class ScreenRenderer {
      private final int x;
      private final int y;
      private final int w;
      private final int h;
      private final Screen scr;
      public final ResourceLocation id;

      public MenuLayoutConfig parent() {
         return MenuLayoutConfig.this;
      }

      public ScreenRenderer(ResourceLocation id, Screen gui, int x, int y, int w, int h) {
         this.scr = gui;
         this.x = x;
         this.y = y;
         this.w = w;
         this.h = h;
         this.id = id;
      }

      private ScreenRenderer(ResourceLocation id, AbstractContainerScreen<?> scrIn) {
         this.x = scrIn.getGuiLeft();
         this.y = scrIn.getGuiTop();
         this.w = scrIn.getXSize();
         this.h = scrIn.getYSize();
         this.scr = scrIn;
         this.id = id;
      }

      public void draw(GuiGraphics g, String c, String s) {
         MenuLayoutConfig.Rect cr = MenuLayoutConfig.this.getComp(c);
         MenuLayoutConfig.Rect sr = MenuLayoutConfig.this.getSide(s);
         g.blit(MenuLayoutConfig.getTexture(this.id), this.x + cr.x, this.y + cr.y, sr.x, sr.y, sr.w, sr.h);
      }

      public void draw(GuiGraphics g, String c, String s, int xoff, int yoff) {
         MenuLayoutConfig.Rect cr = MenuLayoutConfig.this.getComp(c);
         MenuLayoutConfig.Rect sr = MenuLayoutConfig.this.getSide(s);
         g.blit(MenuLayoutConfig.getTexture(this.id), this.x + cr.x + xoff, this.y + cr.y + yoff, sr.x, sr.y, sr.w, sr.h);
      }

      public void drawBottomUp(GuiGraphics g, String c, String s, int prog, int max) {
         if (prog != 0 && max != 0) {
            MenuLayoutConfig.Rect cr = MenuLayoutConfig.this.getComp(c);
            MenuLayoutConfig.Rect sr = MenuLayoutConfig.this.getSide(s);
            int dh = sr.h * prog / max;
            g.blit(MenuLayoutConfig.getTexture(this.id), this.x + cr.x, this.y + cr.y + sr.h - dh, sr.x, sr.y + sr.h - dh, sr.w, dh);
         }
      }

      public void drawLeftRight(GuiGraphics g, String c, String s, int prog, int max) {
         if (prog != 0 && max != 0) {
            MenuLayoutConfig.Rect cr = MenuLayoutConfig.this.getComp(c);
            MenuLayoutConfig.Rect sr = MenuLayoutConfig.this.getSide(s);
            int dw = sr.w * prog / max;
            g.blit(MenuLayoutConfig.getTexture(this.id), this.x + cr.x, this.y + cr.y, sr.x, sr.y, dw, sr.h);
         }
      }

      public void drawLiquid(GuiGraphics g, String c, double per, int height, int sw, int sh) {
         MenuLayoutConfig.Rect cr = MenuLayoutConfig.this.getComp(c);
         int base = cr.y + height;
         int h = (int)Math.round(per * height);
         this.circularBlit(g, this.x + cr.x, base - h, 0, -h, cr.w, h, sw, sh);
      }

      public void start(GuiGraphics g) {
         g.blit(MenuLayoutConfig.getTexture(this.id), this.x, this.y, 0, 0, this.w, this.h);
      }

      private void circularBlit(GuiGraphics g, int sx, int sy, int ix, int iy, int w, int h, int iw, int ih) {
         int x0 = ix;
         int yb = iy;
         int x1 = w;
         int x2 = sx;

         while (x0 < 0) {
            x0 += iw;
         }

         while (yb < ih) {
            yb += ih;
         }

         while (x1 > 0) {
            int dx = Math.min(x1, iw - x0);
            int y0 = yb;
            int y1 = h;
            int y2 = sy;

            while (y1 > 0) {
               int dy = Math.min(y1, ih - y0);
               g.blit(MenuLayoutConfig.getTexture(this.id), x2, y2, x0, y0, x1, y1);
               y1 -= dy;
               y0 += dy;
               y2 += dy;
            }

            x1 -= dx;
            x0 += dx;
            x2 += dx;
         }
      }
   }

   public interface SlotAcceptor {
      void addSlot(String var1, int var2, int var3, Slot var4);
   }

   public interface SlotFactory<T extends Slot> {
      @Nullable
      T getSlot(int var1, int var2);
   }
}

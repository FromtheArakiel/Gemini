package dev.xkmc.l2tabs.compat.jei;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.l2tabs.tabs.core.ITabScreen;
import dev.xkmc.l2tabs.tabs.core.TabGroup;
import javax.annotation.Nullable;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.Screen;

public record SideTabProperties(TabGroup<?> group) {
   @SafeVarargs
   public final void register(IGuiHandlerRegistration event, Class<? extends ITabScreen>... cls) {
      for (Class<? extends ITabScreen> c : cls) {
         event.addGuiScreenHandler((Class)Wrappers.cast(c), e -> this.create((ITabScreen)Wrappers.cast(e)));
      }
   }

   @Nullable
   public IGuiProperties create(ITabScreen screen) {
      if (screen.screenWidth() > 0 && screen.screenHeight() > 0) {
         int w = screen.getXSize();
         int h = screen.getYSize();
         if (w > 0 && h > 0) {
            int x0 = screen.getGuiLeft();
            int y0 = screen.getGuiTop();
            int x1 = x0 + w;
            int y1 = y0 + h;
            int tx = this.group.type.getTabX(w, 0);
            int ty = this.group.type.getTabY(h, 0);
            x0 += Math.min(tx, 0);
            y0 += Math.min(ty, 0);
            x1 = Math.max(x1, x0 + tx + this.group.type.width);
            y1 = Math.max(y1, x0 + ty + this.group.type.height);
            return new GuiProperties(
               (Class<? extends Screen>)screen.asScreen().getClass(), x0, y0, x1 - x0, y1 - y0, screen.screenWidth(), screen.screenHeight()
            );
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}

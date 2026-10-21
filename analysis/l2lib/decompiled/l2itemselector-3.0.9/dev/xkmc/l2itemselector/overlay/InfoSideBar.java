package dev.xkmc.l2itemselector.overlay;

import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;

public abstract class InfoSideBar<S extends SideBar.Signature<S>> extends SideBar<S> implements Layer {
   public InfoSideBar(float duration, float ease) {
      super(duration, ease);
   }

   public void render(GuiGraphics g, DeltaTracker delta) {
      int width = g.guiWidth();
      int height = g.guiHeight();
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         float pt = (float)level.getGameTime() + delta.getGameTimeDeltaPartialTick(true);
         if (this.ease(pt)) {
            List<Component> text = this.getText();
            if (!text.isEmpty()) {
               int anchor = ((InfoSideBar.Anchor)L2ISConfig.CLIENT.infoAnchor.get()).ordinal();
               int y = height * anchor / 2;
               int w = (int)(width * (Double)L2ISConfig.CLIENT.infoMaxWidth.get());
               new TextBox(g, 0, anchor, this.getXOffset(width), y, w).renderLongText(Minecraft.getInstance().font, text);
            }
         }
      }
   }

   protected abstract List<Component> getText();

   @Override
   protected int getXOffset(int width) {
      float progress = (this.max_ease - this.ease_time) / this.max_ease;
      return Math.round(-progress * width / 2.0F + 8.0F);
   }

   public enum Anchor {
      TOP,
      CENTER,
      BOTTOM;
   }
}

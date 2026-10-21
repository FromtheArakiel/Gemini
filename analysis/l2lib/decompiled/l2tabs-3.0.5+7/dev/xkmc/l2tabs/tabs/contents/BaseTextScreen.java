package dev.xkmc.l2tabs.tabs.contents;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import dev.xkmc.l2tabs.tabs.core.ITabScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ScreenEvent.BackgroundRendered;
import net.neoforged.neoforge.common.NeoForge;

public abstract class BaseTextScreen extends Screen implements ITabScreen {
   private final ResourceLocation texture;
   public int imageWidth;
   public int imageHeight;
   public int leftPos;
   public int topPos;

   protected BaseTextScreen(Component title, ResourceLocation texture) {
      super(title);
      this.texture = texture;
      this.imageWidth = 176;
      this.imageHeight = 166;
   }

   public void init() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
   }

   public void renderBackground(GuiGraphics g, int mx, int my, float pt) {
      this.renderTransparentBackground(g);
      NeoForge.EVENT_BUS.post(new BackgroundRendered(this, g));
      int i = this.leftPos;
      int j = this.topPos;
      g.blit(this.texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
   }

   public boolean keyPressed(int a, int b, int c) {
      Key mouseKey = InputConstants.getKey(a, b);
      if (Minecraft.getInstance().options.keyInventory.isActiveAndMatches(mouseKey)) {
         this.onClose();
         return true;
      } else {
         return super.keyPressed(a, b, c);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   @Override
   public int getGuiLeft() {
      return this.leftPos;
   }

   @Override
   public int getGuiTop() {
      return this.topPos;
   }

   @Override
   public int getXSize() {
      return this.imageWidth;
   }

   @Override
   public int getYSize() {
      return this.imageHeight;
   }
}

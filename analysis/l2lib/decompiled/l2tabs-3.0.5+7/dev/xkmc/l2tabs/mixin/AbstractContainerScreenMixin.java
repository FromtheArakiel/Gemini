package dev.xkmc.l2tabs.mixin;

import dev.xkmc.l2tabs.tabs.contents.TabInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
   protected AbstractContainerScreenMixin(Component pTitle) {
      super(pTitle);
   }

   @Inject(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"
      ),
      method = "renderBackground"
   )
   public void l2tabs$renderTabs(GuiGraphics g, int mx, int my, float pt, CallbackInfo ci) {
      TabInventory.renderTabs(g, this);
   }
}

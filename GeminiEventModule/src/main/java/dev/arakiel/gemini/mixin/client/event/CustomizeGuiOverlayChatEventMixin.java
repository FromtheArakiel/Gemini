package dev.arakiel.gemini.mixin.client.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.CustomizeGuiOverlayEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Gui.class)
public abstract class CustomizeGuiOverlayChatEventMixin {
    @Inject(method = "renderChat", at = @At("HEAD"), cancellable = true)
    private void gemini$chatOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        CustomizeGuiOverlayEvent event = new CustomizeGuiOverlayEvent(
                graphics,
                new ArrayList<>(),
                CustomizeGuiOverlayEvent.Phase.CHAT
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

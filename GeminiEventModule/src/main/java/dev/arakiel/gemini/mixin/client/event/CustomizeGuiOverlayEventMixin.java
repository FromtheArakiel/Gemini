package dev.arakiel.gemini.mixin.client.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.CustomizeGuiOverlayEvent;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(net.minecraft.client.gui.components.DebugScreenOverlay.class)
public abstract class CustomizeGuiOverlayEventMixin {
    @Inject(method = "renderLines", at = @At("HEAD"), cancellable = true)
    private void gemini$debugOverlay(
            GuiGraphics graphics,
            List<String> lines,
            boolean left,
            CallbackInfo callback
    ) {
        CustomizeGuiOverlayEvent event = new CustomizeGuiOverlayEvent(
                graphics,
                lines,
                CustomizeGuiOverlayEvent.Phase.DEBUG_TEXT
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

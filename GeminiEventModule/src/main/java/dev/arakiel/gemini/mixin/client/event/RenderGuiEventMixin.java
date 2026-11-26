package dev.arakiel.gemini.mixin.client.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.RenderGuiEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class RenderGuiEventMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void gemini$renderGuiPre(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        RenderGuiEvent event = new RenderGuiEvent(graphics, deltaTracker, RenderGuiEvent.Phase.PRE);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void gemini$renderGuiPost(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        GeminiEvents.post(new RenderGuiEvent(graphics, deltaTracker, RenderGuiEvent.Phase.POST));
    }
}

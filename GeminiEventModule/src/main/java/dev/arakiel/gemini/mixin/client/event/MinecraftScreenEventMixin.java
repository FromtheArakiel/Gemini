package dev.arakiel.gemini.mixin.client.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.ScreenEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftScreenEventMixin {
    @Shadow
    public Screen screen;

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void gemini$setScreen(Screen next, CallbackInfo callback) {
        Minecraft client = (Minecraft) (Object) this;
        if (next != null) {
            ScreenEvent opening = new ScreenEvent(
                    client,
                    next,
                    null,
                    0,
                    0,
                    0.0F,
                    ScreenEvent.Phase.OPENING
            );
            GeminiEvents.post(opening);
            if (opening.isCanceled()) {
                callback.cancel();
            }
        }
    }
}

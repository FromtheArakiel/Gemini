package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.explosion.ExplosionStartEvent;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class ExplosionStartEventMixin {
    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void gemini$explosionStart(CallbackInfo callback) {
        ExplosionStartEvent event = new ExplosionStartEvent((Explosion) (Object) this);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingKnockbackEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingKnockbackEventMixin {
    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void gemini$livingKnockback(double strength, double xRatio, double zRatio, CallbackInfo callback) {
        LivingKnockbackEvent event =
                new LivingKnockbackEvent((LivingEntity) (Object) this, strength, xRatio, zRatio);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

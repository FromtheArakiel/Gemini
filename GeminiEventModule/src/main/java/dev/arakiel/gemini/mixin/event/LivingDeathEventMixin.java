package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingDeathEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingDeathEventMixin {
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void gemini$livingDeath(DamageSource source, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        LivingDeathEvent event = new LivingDeathEvent(entity, source);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            if (entity.getHealth() <= 0.0F) {
                entity.setHealth(1.0F);
            }
            callback.cancel();
        }
    }
}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingAttackEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingAttackEventMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void gemini$livingAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        LivingAttackEvent event = new LivingAttackEvent((LivingEntity) (Object) this, source, amount);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }
}

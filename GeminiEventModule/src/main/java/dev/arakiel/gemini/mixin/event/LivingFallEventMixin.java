package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingFallEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingFallEventMixin {
    @Unique
    private LivingFallEvent gemini$fallEvent;

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void gemini$fall(
            float distance,
            float damageMultiplier,
            DamageSource source,
            CallbackInfoReturnable<Boolean> callback
    ) {
        LivingFallEvent event = this.gemini$postFall(distance, damageMultiplier);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float gemini$distance(float distance, float damageMultiplier) {
        return this.gemini$postFall(distance, damageMultiplier).getDistance();
    }

    @ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private float gemini$damageMultiplier(float damageMultiplier, float distance) {
        return this.gemini$postFall(distance, damageMultiplier).getDamageMultiplier();
    }

    @Unique
    private LivingFallEvent gemini$postFall(float distance, float damageMultiplier) {
        if (this.gemini$fallEvent == null) {
            this.gemini$fallEvent = new LivingFallEvent(
                    (LivingEntity) (Object) this,
                    distance,
                    damageMultiplier
            );
            GeminiEvents.post(this.gemini$fallEvent);
        }
        return this.gemini$fallEvent;
    }
}

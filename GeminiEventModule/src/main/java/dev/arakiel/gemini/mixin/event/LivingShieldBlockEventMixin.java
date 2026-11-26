package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingShieldBlockEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingShieldBlockEventMixin {
    @Shadow
    protected abstract void hurtCurrentlyUsedShield(float amount);

    @Unique
    private LivingShieldBlockEvent gemini$shieldBlockEvent;

    @Inject(method = "hurt", at = @At("HEAD"))
    private void gemini$postShieldBlock(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (amount <= 0.0F) {
            this.gemini$shieldBlockEvent = null;
            return;
        }
        LivingEntity entity = (LivingEntity) (Object) this;
        boolean originalBlocked = entity.isDamageSourceBlocked(source);
        this.gemini$shieldBlockEvent = new LivingShieldBlockEvent(entity, source, amount, originalBlocked);
        GeminiEvents.post(this.gemini$shieldBlockEvent);
    }

    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"
            )
    )
    private boolean gemini$useBlockedResult(LivingEntity entity, DamageSource source) {
        LivingShieldBlockEvent event = this.gemini$shieldBlockEvent;
        return event != null && !event.isCanceled() && event.getBlocked();
    }

    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"
            )
    )
    private void gemini$damageShield(LivingEntity entity, float amount) {
        LivingShieldBlockEvent event = this.gemini$shieldBlockEvent;
        this.hurtCurrentlyUsedShield(event == null ? amount : event.getShieldDamage());
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void gemini$clearShieldEvent(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> callback
    ) {
        this.gemini$shieldBlockEvent = null;
    }
}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingIncomingDamageEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingIncomingDamageEventMixin {
    @Unique
    private LivingIncomingDamageEvent gemini$incomingDamageEvent;

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float gemini$incomingDamage(float amount, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        this.gemini$incomingDamageEvent = null;
        if (entity.isInvulnerableTo(source)
                || entity.level().isClientSide
                || entity.isDeadOrDying()
                || source.is(DamageTypeTags.IS_FIRE) && entity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return amount;
        }
        this.gemini$incomingDamageEvent = new LivingIncomingDamageEvent(entity, source, amount);
        GeminiEvents.post(this.gemini$incomingDamageEvent);
        return this.gemini$incomingDamageEvent.isCanceled()
                ? 0.0F
                : this.gemini$incomingDamageEvent.getAmount();
    }

    @Inject(method = "hurt", at = @At("RETURN"), cancellable = true)
    private void gemini$applyIncomingDamageCancellation(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (this.gemini$incomingDamageEvent != null && this.gemini$incomingDamageEvent.isCanceled()) {
            callback.setReturnValue(false);
        }
        this.gemini$incomingDamageEvent = null;
    }
}

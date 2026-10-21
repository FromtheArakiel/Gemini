package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.EventResult;
import dev.arakiel.gemini.event.effect.MobEffectAddedEvent;
import dev.arakiel.gemini.event.effect.MobEffectApplicableEvent;
import dev.arakiel.gemini.event.effect.MobEffectRemovedEvent;
import dev.arakiel.gemini.event.entity.living.LivingAttackEvent;
import dev.arakiel.gemini.event.entity.living.LivingDamageEvent;
import dev.arakiel.gemini.event.entity.living.LivingDeathEvent;
import dev.arakiel.gemini.event.entity.living.LivingHealEvent;
import dev.arakiel.gemini.event.entity.living.LivingHurtEvent;
import dev.arakiel.gemini.event.entity.living.LivingJumpEvent;
import dev.arakiel.gemini.event.entity.living.LivingKnockbackEvent;
import dev.arakiel.gemini.event.item.ItemUseFinishEvent;
import dev.arakiel.gemini.event.item.ItemUseStartEvent;
import dev.arakiel.gemini.event.item.ItemUseStopEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private MobEffectInstance gemini$previousEffect;

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void gemini$livingAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player) {
            return;
        }
        LivingAttackEvent event = new LivingAttackEvent(entity, source, amount);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float gemini$livingHurt(float amount, DamageSource source) {
        LivingHurtEvent event = new LivingHurtEvent((LivingEntity) (Object) this, source, amount);
        GeminiEvents.post(event);
        return event.isCanceled() ? 0.0F : event.getAmount();
    }

    @ModifyVariable(
            method = "actuallyHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setAbsorptionAmount(F)V",
                    shift = At.Shift.AFTER
            ),
            argsOnly = true,
            ordinal = 0
    )
    private float gemini$livingDamage(float amount, DamageSource source) {
        LivingDamageEvent event = new LivingDamageEvent((LivingEntity) (Object) this, source, amount);
        GeminiEvents.post(event);
        return event.isCanceled() ? 0.0F : event.getAmount();
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float gemini$livingHeal(float amount) {
        LivingHealEvent event = new LivingHealEvent((LivingEntity) (Object) this, amount);
        GeminiEvents.post(event);
        return event.isCanceled() ? 0.0F : event.getAmount();
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void gemini$livingDeath(DamageSource source, CallbackInfo callback) {
        LivingDeathEvent event = new LivingDeathEvent((LivingEntity) (Object) this, source);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            LivingEntity entity = (LivingEntity) (Object) this;
            if (entity.getHealth() <= 0.0F) {
                entity.setHealth(1.0F);
            }
            callback.cancel();
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void gemini$livingJump(CallbackInfo callback) {
        GeminiEvents.post(new LivingJumpEvent((LivingEntity) (Object) this));
    }

    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void gemini$livingKnockback(double strength, double xRatio, double zRatio, CallbackInfo callback) {
        LivingKnockbackEvent event = new LivingKnockbackEvent((LivingEntity) (Object) this, strength, xRatio, zRatio);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void gemini$itemUseStart(InteractionHand hand, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemUseStartEvent event = new ItemUseStartEvent(entity, entity.getItemInHand(hand), hand);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"), cancellable = true)
    private void gemini$itemUseFinish(CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemUseFinishEvent event = new ItemUseFinishEvent(entity, entity.getUseItem());
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void gemini$itemUseStop(CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        GeminiEvents.post(new ItemUseStopEvent(entity, entity.getUseItem()));
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"))
    private void gemini$captureEffect(MobEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        gemini$previousEffect = entity.getEffect(effect.getEffect());
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
    private void gemini$effectAdded(MobEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValue()) {
            GeminiEvents.post(new MobEffectAddedEvent((LivingEntity) (Object) this, effect, gemini$previousEffect, source));
        }
        gemini$previousEffect = null;
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    private void gemini$effectApplicable(MobEffectInstance effect, CallbackInfoReturnable<Boolean> callback) {
        MobEffectApplicableEvent event = new MobEffectApplicableEvent((LivingEntity) (Object) this, effect);
        GeminiEvents.post(event);
        if (event.getResult() != EventResult.DEFAULT) {
            callback.setReturnValue(event.getResult() == EventResult.ALLOW);
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("HEAD"))
    private void gemini$effectRemoved(MobEffectInstance effect, CallbackInfo callback) {
        GeminiEvents.post(new MobEffectRemovedEvent((LivingEntity) (Object) this, effect));
    }
}

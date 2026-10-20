package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.api.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingAttackEvent;
import dev.arakiel.gemini.event.entity.living.LivingDamageEvent;
import dev.arakiel.gemini.event.entity.living.LivingHurtEvent;
import dev.arakiel.gemini.event.entity.player.PlayerAttackEntityEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void gemini$playerAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        LivingAttackEvent event = new LivingAttackEvent((Player) (Object) this, source, amount);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float gemini$playerHurt(float amount, DamageSource source) {
        LivingHurtEvent event = new LivingHurtEvent((Player) (Object) this, source, amount);
        GeminiEvents.post(event);
        return event.isCanceled() ? 0.0F : event.getAmount();
    }

    @ModifyVariable(
            method = "actuallyHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;setAbsorptionAmount(F)V",
                    shift = At.Shift.AFTER
            ),
            argsOnly = true,
            ordinal = 0
    )
    private float gemini$playerDamage(float amount, DamageSource source) {
        LivingDamageEvent event = new LivingDamageEvent((Player) (Object) this, source, amount);
        GeminiEvents.post(event);
        return event.isCanceled() ? 0.0F : event.getAmount();
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void gemini$attackEntity(Entity target, CallbackInfo callback) {
        PlayerAttackEntityEvent event = new PlayerAttackEntityEvent((Player) (Object) this, target);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

}

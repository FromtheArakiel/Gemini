package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.combat.CriticalHitEvent;
import dev.arakiel.gemini.event.combat.SweepAttackEvent;
import dev.arakiel.gemini.event.internal.GeminiAttackContext;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class CriticalHitEventMixin implements GeminiAttackContext {
    @Unique
    private Entity gemini$attackTarget;

    @Unique
    private CriticalHitEvent gemini$criticalHitEvent;

    @Unique
    private SweepAttackEvent gemini$sweepAttackEvent;

    @Inject(method = "attack", at = @At("HEAD"))
    private void gemini$captureAttackTarget(Entity target, CallbackInfo callback) {
        this.gemini$attackTarget = target;
        this.gemini$criticalHitEvent = null;
        this.gemini$sweepAttackEvent = null;
    }

    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getEnchantedDamage(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;)F",
                    ordinal = 0
            )
    )
    private void gemini$criticalHitSetup(Entity target, CallbackInfo callback) {
        Player player = (Player) (Object) this;
        boolean critical = this.gemini$isVanillaCritical(player, target);
        CriticalHitEvent event = new CriticalHitEvent(
                player,
                target,
                critical ? 1.5F : 1.0F,
                critical
        );
        GeminiEvents.post(event);
        this.gemini$criticalHitEvent = event;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                    ordinal = 0
            )
    )
    private boolean gemini$criticalHit(Entity target, DamageSource source, float amount) {
        Player player = (Player) (Object) this;
        CriticalHitEvent event = this.gemini$criticalHitEvent;
        if (event == null || event.getTarget() != target) {
            boolean critical = this.gemini$isVanillaCritical(player, target);
            event = new CriticalHitEvent(player, target, critical ? 1.5F : 1.0F, critical);
            GeminiEvents.post(event);
            this.gemini$criticalHitEvent = event;
        }

        float adjusted = amount;
        if (event.isVanillaCritical() && event.getVanillaMultiplier() > 0.0F) {
            adjusted /= event.getVanillaMultiplier();
        }
        if (event.isCriticalHit()) {
            adjusted *= event.getDamageMultiplier();
        }
        return target.hurt(source, adjusted);
    }

    @Inject(method = "attack", at = @At("RETURN"))
    private void gemini$clearAttackContext(Entity target, CallbackInfo callback) {
        this.gemini$attackTarget = null;
        this.gemini$criticalHitEvent = null;
        this.gemini$sweepAttackEvent = null;
    }

    @Unique
    private boolean gemini$isVanillaCritical(Player player, Entity target) {
        return this.gemini$attackTarget == target
                && player.getAttackStrengthScale(0.5F) > 0.9F
                && player.fallDistance > 0.0F
                && !player.onGround()
                && !player.onClimbable()
                && !player.isInWater()
                && !player.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS)
                && !player.isPassenger()
                && target instanceof LivingEntity
                && !player.isSprinting();
    }

    @Override
    public CriticalHitEvent gemini$getCriticalHitEvent() {
        return this.gemini$criticalHitEvent;
    }

    @Override
    public void gemini$setCriticalHitEvent(CriticalHitEvent event) {
        this.gemini$criticalHitEvent = event;
    }

    @Override
    public SweepAttackEvent gemini$getSweepAttackEvent() {
        return this.gemini$sweepAttackEvent;
    }

    @Override
    public void gemini$setSweepAttackEvent(SweepAttackEvent event) {
        this.gemini$sweepAttackEvent = event;
    }
}

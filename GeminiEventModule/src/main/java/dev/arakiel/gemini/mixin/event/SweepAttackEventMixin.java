package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.combat.CriticalHitEvent;
import dev.arakiel.gemini.event.combat.SweepAttackEvent;
import dev.arakiel.gemini.event.internal.GeminiAttackContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class SweepAttackEventMixin {
    @Shadow
    public float walkDist;

    @Shadow
    public float walkDistO;

    @Unique
    private Entity gemini$sweepTarget;

    @Unique
    private SweepAttackEvent gemini$sweepEvent;

    @Unique
    private float gemini$attackStrength;

    @Inject(method = "attack", at = @At("HEAD"))
    private void gemini$captureSweepTarget(Entity target, CallbackInfo callback) {
        this.gemini$sweepTarget = target;
        this.gemini$sweepEvent = null;
    }

    @Inject(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getAttackStrengthScale(F)F",
                    ordinal = 0
            )
    )
    private void gemini$sweepAttackSetup(Entity target, CallbackInfo callback) {
        Player player = (Player) (Object) this;
        this.gemini$attackStrength = player.getAttackStrengthScale(0.5F);
        CriticalHitEvent critical = ((GeminiAttackContext) (Object) this).gemini$getCriticalHitEvent();
        boolean critBlocksSweep = critical != null
                && critical.isCriticalHit()
                && critical.disableSweep();
        boolean vanillaSweep = this.gemini$attackStrength > 0.9F
                && !critBlocksSweep
                && !player.isSprinting()
                && player.onGround()
                && this.walkDist - this.walkDistO < player.getSpeed();
        SweepAttackEvent event = new SweepAttackEvent(player, target, vanillaSweep);
        GeminiEvents.post(event);
        this.gemini$sweepEvent = event;
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getSpeed()F"
            )
    )
    private float gemini$applySweepState(Player player) {
        SweepAttackEvent event = this.gemini$sweepEvent;
        if (event == null || event.isCanceled() || !event.isSweeping()) {
            return 0.0F;
        }
        return player.getSpeed();
    }

    @Inject(method = "attack", at = @At("RETURN"))
    private void gemini$clearSweepContext(Entity target, CallbackInfo callback) {
        this.gemini$sweepTarget = null;
        this.gemini$sweepEvent = null;
        this.gemini$attackStrength = 0.0F;
    }

}

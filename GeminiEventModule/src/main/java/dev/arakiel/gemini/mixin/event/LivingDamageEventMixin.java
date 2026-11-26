package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingDamageEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class, Player.class})
public abstract class LivingDamageEventMixin {
    @Unique
    private LivingDamageEvent gemini$damagePreEvent;

    @ModifyVariable(
            method = "actuallyHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getAbsorptionAmount()F",
                    shift = At.Shift.BEFORE
            ),
            argsOnly = true,
            ordinal = 0
    )
    private float gemini$livingDamagePre(float amount, DamageSource source) {
        this.gemini$damagePreEvent = new LivingDamageEvent(
                (LivingEntity) (Object) this,
                source,
                amount,
                amount,
                LivingDamageEvent.Phase.PRE
        );
        GeminiEvents.post(this.gemini$damagePreEvent);
        return this.gemini$damagePreEvent.getNewDamage();
    }

    @Inject(method = "actuallyHurt", at = @At("RETURN"))
    private void gemini$livingDamagePost(DamageSource source, float amount, CallbackInfo callback) {
        if (this.gemini$damagePreEvent != null) {
            LivingDamageEvent post = new LivingDamageEvent(
                    (LivingEntity) (Object) this,
                    source,
                    this.gemini$damagePreEvent.getOriginalAmount(),
                    this.gemini$damagePreEvent.getNewDamage(),
                    LivingDamageEvent.Phase.POST
            );
            GeminiEvents.post(post);
        }
        this.gemini$damagePreEvent = null;
    }
}

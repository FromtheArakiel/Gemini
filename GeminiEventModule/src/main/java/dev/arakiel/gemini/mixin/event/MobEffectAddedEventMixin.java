package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.effect.MobEffectAddedEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MobEffectAddedEventMixin {
    @Unique
    private MobEffectInstance gemini$previousEffect;

    @Inject(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD")
    )
    private void gemini$captureEffect(
            MobEffectInstance effect,
            Entity source,
            CallbackInfoReturnable<Boolean> callback
    ) {
        this.gemini$previousEffect = ((LivingEntity) (Object) this).getEffect(effect.getEffect());
    }

    @Inject(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("RETURN")
    )
    private void gemini$effectAdded(
            MobEffectInstance effect,
            Entity source,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (callback.getReturnValue()) {
            GeminiEvents.post(new MobEffectAddedEvent(
                    (LivingEntity) (Object) this,
                    effect,
                    this.gemini$previousEffect,
                    source
            ));
        }
        this.gemini$previousEffect = null;
    }
}

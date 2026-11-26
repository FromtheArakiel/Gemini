package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.EventResult;
import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.effect.MobEffectApplicableEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MobEffectApplicableEventMixin {
    @Inject(method = "canBeAffected", at = @At("RETURN"), cancellable = true)
    private void gemini$effectApplicable(
            MobEffectInstance effect,
            CallbackInfoReturnable<Boolean> callback
    ) {
        MobEffectApplicableEvent event =
                new MobEffectApplicableEvent((LivingEntity) (Object) this, effect);
        GeminiEvents.post(event);
        if (event.getResult() != EventResult.DEFAULT) {
            callback.setReturnValue(event.getResult() == EventResult.ALLOW);
        }
    }
}

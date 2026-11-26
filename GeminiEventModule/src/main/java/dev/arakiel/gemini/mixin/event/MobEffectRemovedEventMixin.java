package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.effect.MobEffectRemovedEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MobEffectRemovedEventMixin {
    @Inject(method = "onEffectRemoved", at = @At("HEAD"))
    private void gemini$effectRemoved(MobEffectInstance effect, CallbackInfo callback) {
        GeminiEvents.post(new MobEffectRemovedEvent((LivingEntity) (Object) this, effect));
    }
}

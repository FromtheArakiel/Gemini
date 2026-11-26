package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingJumpEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingJumpEventMixin {
    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void gemini$livingJump(CallbackInfo callback) {
        GeminiEvents.post(new LivingJumpEvent((LivingEntity) (Object) this));
    }
}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingChangeTargetEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class LivingChangeTargetEventMixin {
    @Unique
    private boolean gemini$applyingTarget;

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void gemini$changeTarget(@Nullable LivingEntity target, CallbackInfo callback) {
        if (this.gemini$applyingTarget) {
            return;
        }
        LivingChangeTargetEvent event = new LivingChangeTargetEvent((Mob) (Object) this, target);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
            return;
        }
        if (event.getNewTarget() != target) {
            callback.cancel();
            this.gemini$applyingTarget = true;
            try {
                ((Mob) (Object) this).setTarget(event.getNewTarget());
            } finally {
                this.gemini$applyingTarget = false;
            }
        }
    }
}

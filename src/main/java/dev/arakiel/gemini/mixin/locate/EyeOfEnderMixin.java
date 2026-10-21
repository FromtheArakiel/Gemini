package dev.arakiel.gemini.mixin.locate;

import dev.arakiel.gemini.locate.internal.PendingEye;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EyeOfEnder.class)
public abstract class EyeOfEnderMixin implements PendingEye {
    @Unique
    private boolean gemini$locating;

    @Override
    public boolean gemini$isLocating() {
        return gemini$locating;
    }

    @Override
    public void gemini$setLocating(boolean locating) {
        gemini$locating = locating;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void gemini$pauseWhileLocating(CallbackInfo callback) {
        if (gemini$locating) {
            callback.cancel();
        }
    }
}

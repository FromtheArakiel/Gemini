package dev.arakiel.gemini.mixin.content;

import dev.arakiel.gemini.api.GeminiApi;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @Shadow
    private int life;

    @Inject(method = "tickDespawn", at = @At("HEAD"))
    private void gemini$applyFastDespawn(CallbackInfo callback) {
        int factor = GeminiApi.getArrowDespawnFactor((AbstractArrow) (Object) this);
        if (factor > 1) {
            life += factor - 1;
        }
    }
}

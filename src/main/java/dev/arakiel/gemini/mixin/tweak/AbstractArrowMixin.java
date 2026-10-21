package dev.arakiel.gemini.mixin.tweak;

import dev.arakiel.gemini.tweak.GeminiTweaks;
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
        int factor = GeminiTweaks.getArrowDespawnFactor((AbstractArrow) (Object) this);
        if (factor > 1) {
            life += factor - 1;
        }
    }
}

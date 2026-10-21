package dev.arakiel.gemini.mixin.tweak;

import dev.arakiel.gemini.tweak.GeminiTweaks;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {
    @Inject(method = "spawnFire", at = @At("HEAD"), cancellable = true)
    private void gemini$cancelSafeLightningFire(int extraIgnitions, CallbackInfo callback) {
        if (GeminiTweaks.isLightningSafe((LightningBolt) (Object) this)) {
            callback.cancel();
        }
    }
}

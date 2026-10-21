package dev.arakiel.gemini.mixin.content;

import dev.arakiel.gemini.api.GeminiApi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    private void gemini$cancelSafeLightningDamage(ServerLevel level, LightningBolt bolt, CallbackInfo callback) {
        if (GeminiApi.isLightningSafe(bolt)) {
            callback.cancel();
        }
    }
}

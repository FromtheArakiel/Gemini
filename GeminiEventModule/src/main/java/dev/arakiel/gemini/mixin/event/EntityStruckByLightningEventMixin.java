package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.EntityStruckByLightningEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityStruckByLightningEventMixin {
    @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    private void gemini$struckByLightning(
            ServerLevel level,
            LightningBolt lightning,
            CallbackInfo callback
    ) {
        EntityStruckByLightningEvent event =
                new EntityStruckByLightningEvent((Entity) (Object) this, lightning);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

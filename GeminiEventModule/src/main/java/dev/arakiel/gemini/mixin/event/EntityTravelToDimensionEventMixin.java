package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.EntityTravelToDimensionEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class, ServerPlayer.class})
public abstract class EntityTravelToDimensionEventMixin {
    @Inject(method = "changeDimension", at = @At("HEAD"), cancellable = true)
    private void gemini$changeDimension(
            DimensionTransition transition,
            CallbackInfoReturnable<Entity> callback
    ) {
        EntityTravelToDimensionEvent event = new EntityTravelToDimensionEvent(
                (Entity) (Object) this,
                transition.newLevel().dimension()
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(null);
        }
    }
}

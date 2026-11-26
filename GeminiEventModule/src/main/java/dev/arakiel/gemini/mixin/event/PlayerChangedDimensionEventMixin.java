package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerChangedDimensionEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class PlayerChangedDimensionEventMixin {
    @Unique
    private ResourceKey<Level> gemini$fromDimension;

    @Inject(method = "changeDimension", at = @At("HEAD"))
    private void gemini$captureDimension(
            DimensionTransition transition,
            CallbackInfoReturnable<Entity> callback
    ) {
        this.gemini$fromDimension = ((ServerPlayer) (Object) this).serverLevel().dimension();
    }

    @Inject(method = "changeDimension", at = @At("RETURN"))
    private void gemini$changedDimension(
            DimensionTransition transition,
            CallbackInfoReturnable<Entity> callback
    ) {
        if (callback.getReturnValue() != null) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            GeminiEvents.post(new PlayerChangedDimensionEvent(
                    player,
                    this.gemini$fromDimension,
                    transition.newLevel().dimension()
            ));
        }
        this.gemini$fromDimension = null;
    }
}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.item.ItemUseStartEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class ItemUseStartEventMixin {
    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void gemini$itemUseStart(InteractionHand hand, CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemUseStartEvent event = new ItemUseStartEvent(entity, entity.getItemInHand(hand), hand);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

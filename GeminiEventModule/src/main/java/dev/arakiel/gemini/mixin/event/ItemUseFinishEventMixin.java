package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.item.ItemUseFinishEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class ItemUseFinishEventMixin {
    @Inject(method = "completeUsingItem", at = @At("HEAD"), cancellable = true)
    private void gemini$itemUseFinish(CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemUseFinishEvent event = new ItemUseFinishEvent(entity, entity.getUseItem());
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

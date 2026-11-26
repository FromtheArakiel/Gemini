package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.item.ItemUseStopEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class ItemUseStopEventMixin {
    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void gemini$itemUseStop(CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        GeminiEvents.post(new ItemUseStopEvent(entity, entity.getUseItem()));
    }
}

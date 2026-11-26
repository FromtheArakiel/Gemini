package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingSwapItemsEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingSwapItemsEventMixin {
    @Inject(method = "swapHandItems", at = @At("HEAD"), cancellable = true)
    private void gemini$swapItems(CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        LivingSwapItemsEvent event = new LivingSwapItemsEvent(entity);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
            return;
        }
        entity.setItemSlot(EquipmentSlot.OFFHAND, event.getItemSwappedToOffHand());
        entity.setItemSlot(EquipmentSlot.MAINHAND, event.getItemSwappedToMainHand());
        callback.cancel();
    }
}

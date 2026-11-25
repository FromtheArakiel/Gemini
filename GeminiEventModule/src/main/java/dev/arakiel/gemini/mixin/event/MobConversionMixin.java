package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingConversionEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobConversionMixin {
    @Inject(method = "convertTo", at = @At("HEAD"), cancellable = true)
    private <T extends Mob> void gemini$conversionPre(
            EntityType<T> targetType,
            boolean keepEquipment,
            CallbackInfoReturnable<T> callback
    ) {
        LivingConversionEvent event = new LivingConversionEvent(
                (Mob) (Object) this,
                targetType,
                LivingConversionEvent.Phase.PRE,
                null
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(null);
        }
    }

    @Inject(method = "convertTo", at = @At("RETURN"))
    private <T extends Mob> void gemini$conversionPost(
            EntityType<T> targetType,
            boolean keepEquipment,
            CallbackInfoReturnable<T> callback
    ) {
        GeminiEvents.post(new LivingConversionEvent(
                (Mob) (Object) this,
                targetType,
                LivingConversionEvent.Phase.POST,
                callback.getReturnValue()
        ));
    }
}

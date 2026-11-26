package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingHealEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingHealEventMixin {
    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float gemini$livingHeal(float amount) {
        LivingHealEvent event = new LivingHealEvent((LivingEntity) (Object) this, amount);
        GeminiEvents.post(event);
        return event.isCanceled() ? 0.0F : event.getAmount();
    }
}

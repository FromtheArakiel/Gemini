package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingHurtEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({LivingEntity.class, Player.class})
public abstract class LivingHurtEventMixin {
    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float gemini$livingHurt(float amount, DamageSource source) {
        LivingHurtEvent event = new LivingHurtEvent((LivingEntity) (Object) this, source, amount);
        GeminiEvents.post(event);
        return event.isCanceled() ? 0.0F : event.getAmount();
    }
}

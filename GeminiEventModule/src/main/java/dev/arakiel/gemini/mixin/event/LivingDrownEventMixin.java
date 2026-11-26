package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingDrownEvent;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingDrownEventMixin {
    @Unique
    private LivingDrownEvent gemini$drownEvent;

    @Unique
    private int gemini$bubbleIndex;

    @Redirect(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
                    ordinal = 0
            )
    )
    private void gemini$drowningBubble(
            Level level,
            ParticleOptions particle,
            double x,
            double y,
            double z,
            double velocityX,
            double velocityY,
            double velocityZ
    ) {
        LivingEntity entity = (LivingEntity) (Object) this;
        LivingDrownEvent event = this.gemini$ensureDrownEvent(entity);
        if (event.isCanceled() || !event.isDrowning()) {
            this.gemini$bubbleIndex++;
            return;
        }

        if (this.gemini$bubbleIndex < Math.min(event.getBubbleCount(), 8)) {
            level.addParticle(particle, x, y, z, velocityX, velocityY, velocityZ);
        } else if (this.gemini$bubbleIndex == 7 && event.getBubbleCount() > 8) {
            for (int index = 8; index < event.getBubbleCount(); index++) {
                level.addParticle(
                        ParticleTypes.BUBBLE,
                        entity.getX() + entity.getRandom().nextDouble() - entity.getRandom().nextDouble(),
                        entity.getY() + entity.getRandom().nextDouble() - entity.getRandom().nextDouble(),
                        entity.getZ() + entity.getRandom().nextDouble() - entity.getRandom().nextDouble(),
                        velocityX,
                        velocityY,
                        velocityZ
                );
            }
        }
        this.gemini$bubbleIndex++;
    }

    @Redirect(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                    ordinal = 2
            )
    )
    private boolean gemini$drown(LivingEntity entity, DamageSource source, float amount) {
        LivingDrownEvent event = this.gemini$ensureDrownEvent(entity);
        boolean applyDamage = true;
        if (event.isCanceled() || !event.isDrowning() || event.getDamageAmount() <= 0.0F) {
            applyDamage = false;
        }
        float damage = event.getDamageAmount();
        this.gemini$drownEvent = null;
        this.gemini$bubbleIndex = 0;
        return applyDamage && entity.hurt(source, damage);
    }

    @Unique
    private LivingDrownEvent gemini$ensureDrownEvent(LivingEntity entity) {
        if (this.gemini$drownEvent == null) {
            this.gemini$drownEvent = new LivingDrownEvent(
                    entity,
                    true,
                    2.0F,
                    8,
                    entity.getAirSupply()
            );
            GeminiEvents.post(this.gemini$drownEvent);
        }
        return this.gemini$drownEvent;
    }
}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingBreatheEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingBreatheEventMixin {
    @Unique
    private LivingBreatheEvent gemini$breatheEvent;

    @Shadow
    protected abstract int decreaseAirSupply(int airSupply);

    @Shadow
    protected abstract int increaseAirSupply(int airSupply);

    @Redirect(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;decreaseAirSupply(I)I"
            )
    )
    private int gemini$consumeAir(LivingEntity entity, int airSupply) {
        LivingBreatheEvent event = this.gemini$breathe(airSupply);
        return airSupply - event.getConsumeAirAmount();
    }

    @Redirect(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;increaseAirSupply(I)I"
            )
    )
    private int gemini$refillAir(LivingEntity entity, int airSupply) {
        LivingBreatheEvent event = this.gemini$breathe(airSupply);
        return Math.min(airSupply + event.getRefillAirAmount(), entity.getMaxAirSupply());
    }

    @Inject(method = "baseTick", at = @At("RETURN"))
    private void gemini$clearBreatheEvent(CallbackInfo callback) {
        this.gemini$breatheEvent = null;
    }

    @Unique
    private LivingBreatheEvent gemini$breathe(int airSupply) {
        if (this.gemini$breatheEvent != null) {
            return this.gemini$breatheEvent;
        }

        LivingEntity entity = (LivingEntity) (Object) this;
        boolean inWater = entity.isEyeInFluid(FluidTags.WATER)
                && !entity.level().getBlockState(BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ()))
                .is(Blocks.BUBBLE_COLUMN);
        boolean canBreathe = !inWater
                || entity.canBreatheUnderwater()
                || MobEffectUtil.hasWaterBreathing(entity)
                || entity instanceof Player player && player.getAbilities().invulnerable;
        int consumeAir = Math.max(0, airSupply - this.decreaseAirSupply(airSupply));
        int refillAir = Math.max(0, this.increaseAirSupply(airSupply) - airSupply);
        this.gemini$breatheEvent = new LivingBreatheEvent(entity, canBreathe, consumeAir, refillAir);
        GeminiEvents.post(this.gemini$breatheEvent);
        return this.gemini$breatheEvent;
    }
}

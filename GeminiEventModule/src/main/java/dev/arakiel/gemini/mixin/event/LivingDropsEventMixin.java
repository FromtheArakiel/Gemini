package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingDropsEvent;
import dev.arakiel.gemini.event.internal.GeminiDropCapture;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingDropsEventMixin {
    @Shadow
    protected int lastHurtByPlayerTime;

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void gemini$startDropCapture(ServerLevel level, DamageSource source, CallbackInfo callback) {
        GeminiDropCapture.begin();
    }

    @Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
    private void gemini$finishDropCapture(ServerLevel level, DamageSource source, CallbackInfo callback) {
        List<ItemStack> captured = GeminiDropCapture.finish();
        if (captured == null) {
            return;
        }
        LivingEntity entity = (LivingEntity) (Object) this;
        LivingDropsEvent event = new LivingDropsEvent(
                entity,
                source,
                captured,
                0,
                this.lastHurtByPlayerTime > 0
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            return;
        }
        for (ItemStack stack : event.getDrops()) {
            if (!stack.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), stack.copy()));
            }
        }
    }

}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingDestroyBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RemoveBlockGoal.class)
public abstract class LivingDestroyBlockEventMixin {
    @Shadow
    @Final
    private Mob removerMob;

    @Inject(method = "isValidTarget", at = @At("RETURN"), cancellable = true)
    private void gemini$livingDestroyBlock(
            LevelReader level,
            BlockPos pos,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (!callback.getReturnValue()) {
            return;
        }
        LivingDestroyBlockEvent event = new LivingDestroyBlockEvent(
                this.removerMob,
                pos,
                level.getBlockState(pos)
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }
}

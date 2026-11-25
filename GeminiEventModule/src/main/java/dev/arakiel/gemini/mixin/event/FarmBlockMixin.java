package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.block.FarmlandTrampleEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {
    @Inject(method = "turnToDirt", at = @At("HEAD"), cancellable = true)
    private static void gemini$trample(
            Entity entity,
            BlockState state,
            Level level,
            BlockPos pos,
            CallbackInfo callback
    ) {
        FarmlandTrampleEvent event = new FarmlandTrampleEvent(
                level,
                pos,
                state,
                entity,
                entity.fallDistance
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

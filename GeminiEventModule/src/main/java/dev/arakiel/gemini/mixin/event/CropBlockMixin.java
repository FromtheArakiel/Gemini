package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.block.CropGrowEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {
    @Inject(method = "growCrops", at = @At("HEAD"), cancellable = true)
    private void gemini$cropGrowPre(Level level, BlockPos pos, BlockState state, CallbackInfo callback) {
        CropGrowEvent event = new CropGrowEvent(level, pos, state, CropGrowEvent.Phase.PRE);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "growCrops", at = @At("RETURN"))
    private void gemini$cropGrowPost(Level level, BlockPos pos, BlockState state, CallbackInfo callback) {
        GeminiEvents.post(new CropGrowEvent(
                level,
                pos,
                level.getBlockState(pos),
                CropGrowEvent.Phase.POST
        ));
    }
}

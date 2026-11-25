package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.EventResult;
import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.block.CreateFluidSourceEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {
    @Inject(method = "getNewLiquid", at = @At("RETURN"), cancellable = true)
    private void gemini$createFluidSource(
            Level level,
            BlockPos pos,
            BlockState state,
            CallbackInfoReturnable<FluidState> callback
    ) {
        CreateFluidSourceEvent event = new CreateFluidSourceEvent(level, pos);
        GeminiEvents.post(event);
        if (event.getResult() == EventResult.DENY) {
            callback.setReturnValue(level.getFluidState(pos));
        }
    }
}

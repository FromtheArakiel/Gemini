package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.block.PistonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonStructureResolver.class)
public abstract class PistonStructureResolverMixin {
    @Shadow
    @Final
    private Level level;

    @Shadow
    @Final
    private BlockPos pistonPos;

    @Shadow
    @Final
    private Direction pistonDirection;

    @Shadow
    @Final
    private boolean extending;

    @Inject(method = "resolve", at = @At("HEAD"), cancellable = true)
    private void gemini$pistonPre(CallbackInfoReturnable<Boolean> callback) {
        PistonStructureResolver resolver = (PistonStructureResolver) (Object) this;
        PistonEvent event = new PistonEvent(
                this.level,
                this.pistonPos,
                this.pistonDirection,
                this.extending,
                PistonEvent.Phase.PRE,
                resolver
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "resolve", at = @At("RETURN"))
    private void gemini$pistonPost(CallbackInfoReturnable<Boolean> callback) {
        GeminiEvents.post(new PistonEvent(
                this.level,
                this.pistonPos,
                this.pistonDirection,
                this.extending,
                PistonEvent.Phase.POST,
                (PistonStructureResolver) (Object) this
        ));
    }
}

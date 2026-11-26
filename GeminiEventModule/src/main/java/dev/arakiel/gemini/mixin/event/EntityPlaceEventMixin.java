package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.block.EntityPlaceEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class EntityPlaceEventMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void gemini$entityPlace(
            BlockPlaceContext context,
            CallbackInfoReturnable<InteractionResult> callback
    ) {
        if (context.getLevel().isClientSide) {
            return;
        }
        BlockState state = ((BlockItem) (Object) this).getBlock().getStateForPlacement(context);
        if (state == null) {
            state = ((BlockItem) (Object) this).getBlock().defaultBlockState();
        }
        EntityPlaceEvent event = new EntityPlaceEvent(
                context.getLevel(),
                context.getClickedPos(),
                state,
                context.getPlayer()
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(InteractionResult.FAIL);
        }
    }
}

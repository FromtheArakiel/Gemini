package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerPlaceBlockEvent;
import dev.arakiel.gemini.event.block.EntityPlaceEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void gemini$placeBlock(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> callback) {
        Player player = context.getPlayer();
        if (context.getLevel().isClientSide) {
            return;
        }

        BlockItem item = (BlockItem) (Object) this;
        BlockState state = item.getBlock().getStateForPlacement(context);
        if (state == null) {
            state = item.getBlock().defaultBlockState();
        }

        if (player == null) {
            EntityPlaceEvent event = new EntityPlaceEvent(
                    context.getLevel(),
                    context.getClickedPos(),
                    state,
                    null
            );
            GeminiEvents.post(event);
            if (event.isCanceled()) {
                callback.setReturnValue(InteractionResult.FAIL);
            }
            return;
        }

        PlayerPlaceBlockEvent playerEvent = new PlayerPlaceBlockEvent(
                player,
                context.getClickedPos(),
                state,
                context.getItemInHand()
        );
        GeminiEvents.post(playerEvent);
        EntityPlaceEvent entityEvent = new EntityPlaceEvent(
                context.getLevel(),
                context.getClickedPos(),
                state,
                player
        );
        GeminiEvents.post(entityEvent);
        if (playerEvent.isCanceled() || entityEvent.isCanceled()) {
            callback.setReturnValue(InteractionResult.FAIL);
        }
    }
}

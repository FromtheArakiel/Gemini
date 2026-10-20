package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.api.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerBreakBlockEvent;
import dev.arakiel.gemini.event.entity.player.PlayerInteractBlockEvent;
import dev.arakiel.gemini.event.entity.player.PlayerUseItemEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    protected ServerLevel level;

    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void gemini$breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        PlayerBreakBlockEvent event = new PlayerBreakBlockEvent(
                player,
                pos,
                level.getBlockState(pos)
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void gemini$interactBlock(
            ServerPlayer player,
            Level level,
            ItemStack stack,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> callback
    ) {
        PlayerInteractBlockEvent event = new PlayerInteractBlockEvent(player, hand, stack, hitResult);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void gemini$useItem(
            ServerPlayer player,
            Level level,
            ItemStack stack,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> callback
    ) {
        PlayerUseItemEvent event = new PlayerUseItemEvent(player, hand, stack);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(InteractionResult.FAIL);
        }
    }
}

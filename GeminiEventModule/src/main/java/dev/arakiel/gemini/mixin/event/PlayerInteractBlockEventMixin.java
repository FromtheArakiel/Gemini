package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerInteractBlockEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class PlayerInteractBlockEventMixin {
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
}

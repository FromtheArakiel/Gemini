package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerUseItemEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class PlayerUseItemEventMixin {
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

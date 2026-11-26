package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.level.BonemealEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public abstract class BonemealEventMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void gemini$bonemeal(UseOnContext context, CallbackInfoReturnable<InteractionResult> callback) {
        if (context.getLevel().isClientSide) {
            return;
        }
        BonemealEvent event = new BonemealEvent(
                context.getLevel(),
                context.getClickedPos(),
                context.getLevel().getBlockState(context.getClickedPos()),
                context.getItemInHand()
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(InteractionResult.FAIL);
        }
    }
}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.api.GeminiEvents;
import dev.arakiel.gemini.event.level.BonemealEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void gemini$bonemeal(UseOnContext context, CallbackInfoReturnable<InteractionResult> callback) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return;
        }
        BlockPos pos = context.getClickedPos();
        BonemealEvent event = new BonemealEvent(
                level,
                pos,
                level.getBlockState(pos),
                context.getItemInHand()
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(InteractionResult.FAIL);
        }
    }
}

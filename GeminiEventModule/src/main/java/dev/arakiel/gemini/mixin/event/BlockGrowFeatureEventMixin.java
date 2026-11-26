package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.block.BlockGrowFeatureEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public abstract class BlockGrowFeatureEventMixin {
    @Inject(method = "growCrop", at = @At("HEAD"), cancellable = true)
    private static void gemini$growFeature(
            ItemStack stack,
            Level level,
            BlockPos pos,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockGrowFeatureEvent event = new BlockGrowFeatureEvent(
                serverLevel,
                pos,
                null,
                level.getBlockState(pos)
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }
}

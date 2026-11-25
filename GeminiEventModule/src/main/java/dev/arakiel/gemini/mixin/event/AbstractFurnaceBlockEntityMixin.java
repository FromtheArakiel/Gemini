package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.block.FurnaceBurnTimeEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    @Inject(method = "getBurnDuration", at = @At("RETURN"), cancellable = true)
    private void gemini$burnTime(ItemStack fuel, CallbackInfoReturnable<Integer> callback) {
        FurnaceBurnTimeEvent event = new FurnaceBurnTimeEvent(
                (AbstractFurnaceBlockEntity) (Object) this,
                fuel,
                callback.getReturnValue()
        );
        GeminiEvents.post(event);
        if (event.getBurnTime() != callback.getReturnValue()) {
            callback.setReturnValue(event.getBurnTime());
        }
    }
}

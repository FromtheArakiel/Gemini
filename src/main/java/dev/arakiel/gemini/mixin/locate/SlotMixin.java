package dev.arakiel.gemini.mixin.locate;

import dev.arakiel.gemini.locate.internal.LocateMaps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void gemini$blockPendingMapPickup(Player player, CallbackInfoReturnable<Boolean> callback) {
        if (LocateMaps.isPending(((Slot) (Object) this).getItem())) {
            callback.setReturnValue(false);
        }
    }
}

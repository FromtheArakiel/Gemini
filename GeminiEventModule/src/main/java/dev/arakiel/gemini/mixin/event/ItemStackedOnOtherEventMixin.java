package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.ItemStackedOnOtherEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackedOnOtherEventMixin {
    @Inject(method = "overrideStackedOnOther", at = @At("HEAD"), cancellable = true)
    private void gemini$stackedOnOther(
            Slot slot,
            ClickAction action,
            Player player,
            CallbackInfoReturnable<Boolean> callback
    ) {
        ItemStack carried = (ItemStack) (Object) this;
        ItemStackedOnOtherEvent event = new ItemStackedOnOtherEvent(
                carried,
                slot.getItem(),
                slot,
                action,
                player.containerMenu
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(true);
        }
    }
}

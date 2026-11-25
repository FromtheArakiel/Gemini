package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerItemSmeltedEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceResultSlot.class)
public abstract class FurnaceResultSlotMixin {
    @Inject(method = "onTake", at = @At("HEAD"))
    private void gemini$smelted(Player player, ItemStack stack, CallbackInfo callback) {
        if (!player.level().isClientSide) {
            GeminiEvents.post(new PlayerItemSmeltedEvent(player, stack));
        }
    }
}

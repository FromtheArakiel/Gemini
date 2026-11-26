package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.brewing.PlayerBrewedPotionEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public abstract class PlayerBrewedPotionEventMixin {
    @Inject(method = "onTake", at = @At("HEAD"))
    private void gemini$playerBrewedPotion(Player player, ItemStack stack, CallbackInfo callback) {
        GeminiEvents.post(new PlayerBrewedPotionEvent(player, stack));
    }
}

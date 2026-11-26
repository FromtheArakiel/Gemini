package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.enchant.PlayerEnchantItemEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public abstract class PlayerEnchantItemEventMixin {
    @Shadow
    @Final
    private Container enchantSlots;

    @Shadow
    public int[] costs;

    @Inject(method = "clickMenuButton", at = @At("HEAD"), cancellable = true)
    private void gemini$playerEnchant(int index, Player player, CallbackInfoReturnable<Boolean> callback) {
        int levels = index >= 0 && index < this.costs.length ? this.costs[index] : 0;
        PlayerEnchantItemEvent event = new PlayerEnchantItemEvent(
                player,
                this.enchantSlots.getItem(0),
                levels
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }
}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.enchant.EnchantmentLevelSetEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentLevelSetEventMixin {
    @Shadow
    @Final
    private Container enchantSlots;

    @Shadow
    public int[] costs;

    @Unique
    private Player gemini$player;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;)V", at = @At("TAIL"))
    private void gemini$initWithoutAccess(int syncId, Inventory inventory, CallbackInfo callback) {
        this.gemini$player = inventory.player;
    }

    @Inject(
            method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
            at = @At("TAIL")
    )
    private void gemini$initWithAccess(
            int syncId,
            Inventory inventory,
            ContainerLevelAccess access,
            CallbackInfo callback
    ) {
        this.gemini$player = inventory.player;
    }

    @Inject(method = "slotsChanged", at = @At("RETURN"))
    private void gemini$enchantmentLevels(Container container, CallbackInfo callback) {
        if (this.gemini$player == null) {
            return;
        }
        for (int row = 0; row < this.costs.length; row++) {
            int original = this.costs[row];
            EnchantmentLevelSetEvent event = new EnchantmentLevelSetEvent(
                    this.gemini$player.level(),
                    this.gemini$player.blockPosition(),
                    row,
                    original,
                    original
            );
            GeminiEvents.post(event);
            this.costs[row] = event.getEnchantLevel();
        }
    }
}

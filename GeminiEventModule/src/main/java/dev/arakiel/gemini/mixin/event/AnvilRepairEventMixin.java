package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.enchant.AnvilRepairEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(AnvilMenu.class)
public abstract class AnvilRepairEventMixin {
    @Shadow
    protected Container inputSlots;

    @Unique
    private AnvilRepairEvent gemini$repairEvent;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void gemini$anvilRepair(Player player, ItemStack output, CallbackInfo callback) {
        this.gemini$repairEvent = new AnvilRepairEvent(
                player,
                this.inputSlots.getItem(0),
                this.inputSlots.getItem(1),
                output,
                0.12F
        );
        GeminiEvents.post(this.gemini$repairEvent);
    }

    @ModifyConstant(method = "onTake", constant = @Constant(floatValue = 0.12F))
    private float gemini$breakChance(float original) {
        return this.gemini$repairEvent == null
                ? original
                : this.gemini$repairEvent.getBreakChance();
    }
}

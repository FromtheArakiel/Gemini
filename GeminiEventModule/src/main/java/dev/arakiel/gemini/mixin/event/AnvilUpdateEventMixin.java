package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.enchant.AnvilUpdateEvent;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilUpdateEventMixin {
    @Shadow
    protected Container inputSlots;

    @Shadow
    @Final
    protected ResultContainer resultSlots;

    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    private String itemName;

    @Inject(method = "createResult", at = @At("RETURN"))
    private void gemini$anvilUpdate(CallbackInfo callback) {
        AnvilUpdateEvent event = new AnvilUpdateEvent(
                this.inputSlots.getItem(0),
                this.inputSlots.getItem(1),
                this.itemName,
                this.resultSlots.getItem(0),
                this.cost.get()
        );
        GeminiEvents.post(event);
        this.resultSlots.setItem(0, event.isCanceled()
                ? net.minecraft.world.item.ItemStack.EMPTY
                : event.getOutput());
        this.cost.set(event.getCost());
    }
}

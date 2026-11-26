package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.enchant.GrindstoneEvent;
import dev.arakiel.gemini.event.internal.GeminiGrindstoneAccess;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneEventMixin implements GeminiGrindstoneAccess {
    @Shadow
    @Final
    private Container repairSlots;

    @Shadow
    @Final
    private Container resultSlots;

    @Unique
    private int gemini$experience = -1;

    @Inject(method = "createResult", at = @At("RETURN"))
    private void gemini$grindstoneResult(CallbackInfo callback) {
        ItemStack output = this.resultSlots.getItem(0);
        if (output.isEmpty()) {
            return;
        }
        GrindstoneEvent.OnPlaceItem event = new GrindstoneEvent.OnPlaceItem(
                this.repairSlots.getItem(0),
                this.repairSlots.getItem(1),
                this.gemini$experience
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.gemini$experience = -1;
            return;
        }

        this.gemini$experience = event.getXp();
        if (!event.getOutput().isEmpty()) {
            this.resultSlots.setItem(0, event.getOutput());
        }
    }

    @Override
    public Container gemini$getRepairSlots() {
        return this.repairSlots;
    }

    @Override
    public int gemini$getExperience() {
        return this.gemini$experience;
    }

    @Override
    public void gemini$setExperience(int experience) {
        this.gemini$experience = experience;
    }
}

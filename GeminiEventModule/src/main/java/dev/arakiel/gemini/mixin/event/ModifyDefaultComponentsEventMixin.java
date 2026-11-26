package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.item.ModifyDefaultComponentsEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ModifyDefaultComponentsEventMixin {
    @Inject(method = "getDefaultInstance", at = @At("RETURN"))
    private void gemini$modifyDefaultComponents(CallbackInfoReturnable<ItemStack> callback) {
        GeminiEvents.post(new ModifyDefaultComponentsEvent(callback.getReturnValue()));
    }
}

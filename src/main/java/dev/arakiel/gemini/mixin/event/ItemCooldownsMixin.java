package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.item.ItemCooldownEndEvent;
import dev.arakiel.gemini.event.item.ItemCooldownStartEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCooldowns.class)
public abstract class ItemCooldownsMixin {
    @Inject(method = "addCooldown", at = @At("HEAD"), cancellable = true)
    private void gemini$cooldownStart(Item item, int ticks, CallbackInfo callback) {
        ItemCooldownStartEvent event = new ItemCooldownStartEvent(item, ticks);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "onCooldownEnded", at = @At("HEAD"))
    private void gemini$cooldownEnd(Item item, CallbackInfo callback) {
        GeminiEvents.post(new ItemCooldownEndEvent(item));
    }
}

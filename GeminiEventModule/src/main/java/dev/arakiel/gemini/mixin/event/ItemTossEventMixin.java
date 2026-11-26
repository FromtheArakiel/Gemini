package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.item.ItemTossEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class ItemTossEventMixin {
    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gemini$tossItem(
            ItemStack stack,
            boolean dropAround,
            boolean traceItem,
            CallbackInfoReturnable<ItemEntity> callback
    ) {
        if (stack.isEmpty()) {
            return;
        }
        ItemTossEvent event = new ItemTossEvent((Player) (Object) this, stack);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(null);
        }
    }
}

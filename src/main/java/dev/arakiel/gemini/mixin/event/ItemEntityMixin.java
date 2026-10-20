package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.api.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerItemPickupEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void gemini$itemPickup(Player player, CallbackInfo callback) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        PlayerItemPickupEvent event = new PlayerItemPickupEvent(player, itemEntity, itemEntity.getItem());
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

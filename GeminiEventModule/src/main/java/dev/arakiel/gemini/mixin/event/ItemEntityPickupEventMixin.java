package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.item.ItemEntityPickupEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityPickupEventMixin {
    @Unique
    private ItemStack gemini$pickupOriginal;

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void gemini$itemPickupPre(Player player, CallbackInfo callback) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        this.gemini$pickupOriginal = itemEntity.getItem().copy();
        ItemEntityPickupEvent event = new ItemEntityPickupEvent(
                player,
                itemEntity,
                itemEntity.getItem(),
                ItemEntityPickupEvent.Phase.PRE
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "playerTouch", at = @At("RETURN"))
    private void gemini$itemPickupPost(Player player, CallbackInfo callback) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (this.gemini$pickupOriginal != null) {
            GeminiEvents.post(new ItemEntityPickupEvent(
                    player,
                    itemEntity,
                    this.gemini$pickupOriginal,
                    ItemEntityPickupEvent.Phase.POST
            ));
        }
        this.gemini$pickupOriginal = null;
    }
}

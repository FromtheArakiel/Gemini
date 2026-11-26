package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerItemPickupEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class PlayerItemPickupEventMixin {
    @Unique
    private boolean gemini$playerPickupPosted;

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void gemini$playerItemPickupPre(Player player, CallbackInfo callback) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        PlayerItemPickupEvent event = new PlayerItemPickupEvent(
                player,
                itemEntity,
                itemEntity.getItem(),
                PlayerItemPickupEvent.Phase.PRE
        );
        GeminiEvents.post(event);
        this.gemini$playerPickupPosted = !event.isCanceled();
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "playerTouch", at = @At("RETURN"))
    private void gemini$playerItemPickupPost(Player player, CallbackInfo callback) {
        if (this.gemini$playerPickupPosted) {
            ItemEntity itemEntity = (ItemEntity) (Object) this;
            GeminiEvents.post(new PlayerItemPickupEvent(
                    player,
                    itemEntity,
                    itemEntity.getItem(),
                    PlayerItemPickupEvent.Phase.POST
            ));
        }
        this.gemini$playerPickupPosted = false;
    }
}

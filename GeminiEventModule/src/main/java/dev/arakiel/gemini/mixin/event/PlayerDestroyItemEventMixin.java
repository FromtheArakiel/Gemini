package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerDestroyItemEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class PlayerDestroyItemEventMixin {
    @Inject(method = "onEquippedItemBroken", at = @At("HEAD"))
    private void gemini$destroyItem(Item item, EquipmentSlot slot, CallbackInfo callback) {
        GeminiEvents.post(new PlayerDestroyItemEvent(
                (ServerPlayer) (Object) this,
                new ItemStack(item),
                null
        ));
    }
}

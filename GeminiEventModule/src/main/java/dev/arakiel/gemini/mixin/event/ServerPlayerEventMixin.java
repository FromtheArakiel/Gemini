package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerChangedDimensionEvent;
import dev.arakiel.gemini.event.entity.player.PlayerContainerEvent;
import dev.arakiel.gemini.event.entity.player.PlayerDestroyItemEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEventMixin {
    @Inject(method = "changeDimension", at = @At("HEAD"), cancellable = true)
    private void gemini$changedDimension(
            DimensionTransition transition,
            CallbackInfoReturnable<Entity> callback
    ) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        PlayerChangedDimensionEvent event = new PlayerChangedDimensionEvent(
                player,
                player.serverLevel().dimension(),
                transition.newLevel().dimension()
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(player);
        }
    }

    @Inject(method = "openMenu", at = @At("RETURN"))
    private void gemini$openContainer(MenuProvider provider, CallbackInfoReturnable<OptionalInt> callback) {
        if (callback.getReturnValue().isPresent()) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            GeminiEvents.post(new PlayerContainerEvent(
                    player,
                    PlayerContainerEvent.Phase.OPEN,
                    player.containerMenu
            ));
        }
    }

    @Inject(method = "doCloseContainer", at = @At("HEAD"))
    private void gemini$closeContainer(CallbackInfo callback) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        GeminiEvents.post(new PlayerContainerEvent(
                player,
                PlayerContainerEvent.Phase.CLOSE,
                player.containerMenu
        ));
    }

    @Inject(method = "onEquippedItemBroken", at = @At("HEAD"))
    private void gemini$destroyItem(Item item, EquipmentSlot slot, CallbackInfo callback) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        GeminiEvents.post(new PlayerDestroyItemEvent(player, new ItemStack(item), null));
    }
}

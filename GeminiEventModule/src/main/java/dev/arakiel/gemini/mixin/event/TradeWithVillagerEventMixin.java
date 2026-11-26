package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.trade.TradeWithVillagerEvent;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractVillager.class)
public abstract class TradeWithVillagerEventMixin {
    @Inject(method = "notifyTrade", at = @At("HEAD"), cancellable = true)
    private void gemini$trade(MerchantOffer offer, CallbackInfo callback) {
        AbstractVillager villager = (AbstractVillager) (Object) this;
        Player player = villager.getTradingPlayer();
        if (player == null) {
            return;
        }
        TradeWithVillagerEvent event = new TradeWithVillagerEvent(player, villager, offer);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

package dev.arakiel.gemini.event.trade;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Objects;

@Cancelable
public class TradeWithVillagerEvent extends GeminiEvent {
    private final Player player;
    private final AbstractVillager villager;
    private final MerchantOffer offer;

    public TradeWithVillagerEvent(Player player, AbstractVillager villager, MerchantOffer offer) {
        this.player = Objects.requireNonNull(player, "player");
        this.villager = Objects.requireNonNull(villager, "villager");
        this.offer = Objects.requireNonNull(offer, "offer");
    }

    public Player getPlayer() {
        return this.player;
    }

    public AbstractVillager getVillager() {
        return this.villager;
    }

    public MerchantOffer getOffer() {
        return this.offer;
    }
}

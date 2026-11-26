package dev.arakiel.gemini.event.trade;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VillagerTradesEvent extends GeminiEvent {
    private final VillagerProfession profession;
    private final Map<Integer, List<VillagerTrades.ItemListing>> trades;

    public VillagerTradesEvent(
            VillagerProfession profession,
            Map<Integer, List<VillagerTrades.ItemListing>> trades
    ) {
        this.profession = Objects.requireNonNull(profession, "profession");
        this.trades = Objects.requireNonNull(trades, "trades");
    }

    public VillagerProfession getProfession() {
        return this.profession;
    }

    public Map<Integer, List<VillagerTrades.ItemListing>> getTrades() {
        return this.trades;
    }

    public List<VillagerTrades.ItemListing> getTrades(int level) {
        return this.trades.computeIfAbsent(level, ignored -> new ArrayList<>());
    }

    public void addTrade(int level, VillagerTrades.ItemListing trade) {
        this.getTrades(level).add(Objects.requireNonNull(trade, "trade"));
    }

    public void removeTrades(int level) {
        this.getTrades(level).clear();
    }
}

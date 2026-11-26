package dev.arakiel.gemini.event.trade;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WandererTradesEvent extends GeminiEvent {
    private final List<VillagerTrades.ItemListing> genericTrades;
    private final List<VillagerTrades.ItemListing> rareTrades;

    public WandererTradesEvent(
            List<VillagerTrades.ItemListing> genericTrades,
            List<VillagerTrades.ItemListing> rareTrades
    ) {
        this.genericTrades = Objects.requireNonNull(genericTrades, "genericTrades");
        this.rareTrades = Objects.requireNonNull(rareTrades, "rareTrades");
    }

    public List<VillagerTrades.ItemListing> getGenericTrades() {
        return this.genericTrades;
    }

    public List<VillagerTrades.ItemListing> getRareTrades() {
        return this.rareTrades;
    }
}

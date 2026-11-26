package dev.arakiel.gemini.event.internal;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.trade.VillagerTradesEvent;
import dev.arakiel.gemini.event.trade.WandererTradesEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GeminiTradeRuntime {
    private static final Set<VillagerProfession> APPLIED_PROFESSIONS = new LinkedHashSet<>();
    private static boolean wandererApplied;

    private GeminiTradeRuntime() {
    }

    public static void applyVillager(VillagerProfession profession) {
        if (!APPLIED_PROFESSIONS.add(profession)) {
            return;
        }
        Int2ObjectMap<VillagerTrades.ItemListing[]> vanillaTrades = VillagerTrades.TRADES.get(profession);
        if (vanillaTrades == null) {
            return;
        }
        Map<Integer, List<VillagerTrades.ItemListing>> mutableTrades = new LinkedHashMap<>();
        for (int level = 1; level <= 5; level++) {
            VillagerTrades.ItemListing[] listings = vanillaTrades.get(level);
            mutableTrades.put(level, listings == null
                    ? new ArrayList<>()
                    : new ArrayList<>(List.of(listings)));
        }
        VillagerTradesEvent event = new VillagerTradesEvent(profession, mutableTrades);
        GeminiEvents.post(event);
        event.getTrades().forEach((level, listings) ->
                vanillaTrades.put(level.intValue(), listings.toArray(VillagerTrades.ItemListing[]::new)));
    }

    public static void applyWanderer() {
        if (wandererApplied) {
            return;
        }
        wandererApplied = true;
        VillagerTrades.ItemListing[] generic = VillagerTrades.WANDERING_TRADER_TRADES.get(1);
        VillagerTrades.ItemListing[] rare = VillagerTrades.WANDERING_TRADER_TRADES.get(2);
        List<VillagerTrades.ItemListing> genericTrades =
                generic == null ? new ArrayList<>() : new ArrayList<>(List.of(generic));
        List<VillagerTrades.ItemListing> rareTrades =
                rare == null ? new ArrayList<>() : new ArrayList<>(List.of(rare));
        WandererTradesEvent event = new WandererTradesEvent(genericTrades, rareTrades);
        GeminiEvents.post(event);
        VillagerTrades.WANDERING_TRADER_TRADES.put(
                1,
                event.getGenericTrades().toArray(VillagerTrades.ItemListing[]::new)
        );
        VillagerTrades.WANDERING_TRADER_TRADES.put(
                2,
                event.getRareTrades().toArray(VillagerTrades.ItemListing[]::new)
        );
    }
}

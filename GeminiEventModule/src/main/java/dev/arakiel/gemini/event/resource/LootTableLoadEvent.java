package dev.arakiel.gemini.event.resource;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Objects;

public class LootTableLoadEvent extends GeminiEvent {
    private final ResourceKey<LootTable> key;
    private final boolean builtIn;
    private LootTable table;

    public LootTableLoadEvent(ResourceKey<LootTable> key, LootTable table, boolean builtIn) {
        this.key = Objects.requireNonNull(key, "key");
        this.table = Objects.requireNonNull(table, "table");
        this.builtIn = builtIn;
    }

    public ResourceKey<LootTable> getKey() {
        return this.key;
    }

    public LootTable getTable() {
        return this.table;
    }

    public void setTable(LootTable table) {
        this.table = Objects.requireNonNull(table, "table");
    }

    public boolean isBuiltIn() {
        return this.builtIn;
    }
}

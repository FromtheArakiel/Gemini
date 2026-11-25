package dev.arakiel.gemini.event.spawn;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.List;
import java.util.Objects;

public class ModifyCustomSpawnersEvent extends GeminiEvent {
    private final ServerLevel level;
    private final MobCategory category;
    private final List<MobSpawnSettings.SpawnerData> spawners;

    public ModifyCustomSpawnersEvent(
            ServerLevel level,
            MobCategory category,
            List<MobSpawnSettings.SpawnerData> spawners
    ) {
        this.level = Objects.requireNonNull(level, "level");
        this.category = Objects.requireNonNull(category, "category");
        this.spawners = Objects.requireNonNull(spawners, "spawners");
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public MobCategory getCategory() {
        return this.category;
    }

    public List<MobSpawnSettings.SpawnerData> getSpawners() {
        return this.spawners;
    }
}

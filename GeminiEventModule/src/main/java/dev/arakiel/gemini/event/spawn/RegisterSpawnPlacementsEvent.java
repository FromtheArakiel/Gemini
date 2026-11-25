package dev.arakiel.gemini.event.spawn;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterSpawnPlacementsEvent extends GeminiEvent {
    private final List<Placement<?>> placements = new ArrayList<>();

    public <T extends Mob> void register(
            EntityType<T> type,
            SpawnPlacementType placementType,
            Heightmap.Types heightmap,
            SpawnPlacements.SpawnPredicate<T> predicate
    ) {
        this.placements.add(new Placement<>(type, placementType, heightmap, predicate));
    }

    public List<Placement<?>> getPlacements() {
        return List.copyOf(this.placements);
    }

    public record Placement<T extends Mob>(
            EntityType<T> type,
            SpawnPlacementType placementType,
            Heightmap.Types heightmap,
            SpawnPlacements.SpawnPredicate<T> predicate
    ) {
        public Placement {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(placementType, "placementType");
            Objects.requireNonNull(heightmap, "heightmap");
            Objects.requireNonNull(predicate, "predicate");
        }
    }
}

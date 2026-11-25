package dev.arakiel.gemini.server;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.spawn.RegisterSpawnPlacementsEvent;
import net.minecraft.world.entity.SpawnPlacements;

import java.util.Objects;

public final class GeminiGameplayHooks {
    private GeminiGameplayHooks() {
    }

    public static RegisterSpawnPlacementsEvent registerSpawnPlacements() {
        RegisterSpawnPlacementsEvent event = new RegisterSpawnPlacementsEvent();
        GeminiEvents.post(event);
        for (RegisterSpawnPlacementsEvent.Placement<?> placement : event.getPlacements()) {
            registerSpawnPlacement(placement);
        }
        return event;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerSpawnPlacement(RegisterSpawnPlacementsEvent.Placement<?> placement) {
        SpawnPlacements.register(
                (net.minecraft.world.entity.EntityType) Objects.requireNonNull(placement.type(), "type"),
                placement.placementType(),
                placement.heightmap(),
                placement.predicate()
        );
    }
}

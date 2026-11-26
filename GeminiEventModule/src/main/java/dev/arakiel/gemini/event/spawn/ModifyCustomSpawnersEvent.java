package dev.arakiel.gemini.event.spawn;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;

import java.util.List;
import java.util.Objects;

public class ModifyCustomSpawnersEvent extends GeminiEvent {
    private final ServerLevel level;
    private final List<CustomSpawner> customSpawners;

    public ModifyCustomSpawnersEvent(ServerLevel level, List<CustomSpawner> customSpawners) {
        this.level = Objects.requireNonNull(level, "level");
        this.customSpawners = Objects.requireNonNull(customSpawners, "customSpawners");
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public List<CustomSpawner> getCustomSpawners() {
        return this.customSpawners;
    }

    public void addCustomSpawner(CustomSpawner customSpawner) {
        this.customSpawners.add(Objects.requireNonNull(customSpawner, "customSpawner"));
    }
}

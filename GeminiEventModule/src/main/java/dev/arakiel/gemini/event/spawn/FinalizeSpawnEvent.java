package dev.arakiel.gemini.event.spawn;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.Objects;

@Cancelable
public class FinalizeSpawnEvent extends GeminiEvent {
    private final Mob mob;
    private final ServerLevelAccessor level;
    private final DifficultyInstance difficulty;
    private final MobSpawnType spawnType;

    public FinalizeSpawnEvent(
            Mob mob,
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType
    ) {
        this.mob = Objects.requireNonNull(mob, "mob");
        this.level = Objects.requireNonNull(level, "level");
        this.difficulty = Objects.requireNonNull(difficulty, "difficulty");
        this.spawnType = Objects.requireNonNull(spawnType, "spawnType");
    }

    public Mob getMob() {
        return this.mob;
    }

    public ServerLevelAccessor getLevel() {
        return this.level;
    }

    public DifficultyInstance getDifficulty() {
        return this.difficulty;
    }

    public MobSpawnType getSpawnType() {
        return this.spawnType;
    }
}

package dev.arakiel.gemini.event.spawn;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.EntityEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Cancelable
public class MobSpawnEvent extends EntityEvent {
    public enum Phase {
        POSITION_CHECK,
        FINALIZE
    }

    private final LevelAccessor level;
    private final double x;
    private final double y;
    private final double z;
    private final MobSpawnType spawnType;
    private final Phase phase;
    private @Nullable SpawnGroupData spawnGroupData;

    public MobSpawnEvent(
            Mob mob,
            LevelAccessor level,
            double x,
            double y,
            double z,
            MobSpawnType spawnType,
            Phase phase,
            @Nullable SpawnGroupData spawnGroupData
    ) {
        super(mob);
        this.level = Objects.requireNonNull(level, "level");
        this.x = x;
        this.y = y;
        this.z = z;
        this.spawnType = Objects.requireNonNull(spawnType, "spawnType");
        this.phase = Objects.requireNonNull(phase, "phase");
        this.spawnGroupData = spawnGroupData;
    }

    public Mob getMob() {
        return (Mob) this.getEntity();
    }

    public LevelAccessor getLevel() {
        return this.level;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public MobSpawnType getSpawnType() {
        return this.spawnType;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public @Nullable SpawnGroupData getSpawnGroupData() {
        return this.spawnGroupData;
    }

    public void setSpawnGroupData(@Nullable SpawnGroupData spawnGroupData) {
        this.spawnGroupData = spawnGroupData;
    }
}

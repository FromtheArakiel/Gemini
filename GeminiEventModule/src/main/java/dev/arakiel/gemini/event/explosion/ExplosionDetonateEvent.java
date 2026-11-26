package dev.arakiel.gemini.event.explosion;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;

import java.util.List;
import java.util.Objects;

public class ExplosionDetonateEvent extends GeminiEvent {
    private final Explosion explosion;
    private final List<Entity> affectedEntities;

    public ExplosionDetonateEvent(Explosion explosion, List<Entity> affectedEntities) {
        this.explosion = Objects.requireNonNull(explosion, "explosion");
        this.affectedEntities = Objects.requireNonNull(affectedEntities, "affectedEntities");
    }

    public Explosion getExplosion() {
        return this.explosion;
    }

    public List<Entity> getAffectedEntities() {
        return this.affectedEntities;
    }

    public List<net.minecraft.core.BlockPos> getAffectedBlocks() {
        return this.explosion.getToBlow();
    }
}

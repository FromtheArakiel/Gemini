package dev.arakiel.gemini.event.explosion;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

@Cancelable
public class ExplosionKnockbackEvent extends GeminiEvent {
    private final Explosion explosion;
    private final Entity entity;
    private Vec3 knockback;

    public ExplosionKnockbackEvent(Explosion explosion, Entity entity, Vec3 knockback) {
        this.explosion = Objects.requireNonNull(explosion, "explosion");
        this.entity = Objects.requireNonNull(entity, "entity");
        this.knockback = Objects.requireNonNull(knockback, "knockback");
    }

    public Explosion getExplosion() {
        return this.explosion;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Vec3 getKnockback() {
        return this.knockback;
    }

    public void setKnockback(Vec3 knockback) {
        this.knockback = Objects.requireNonNull(knockback, "knockback");
    }
}

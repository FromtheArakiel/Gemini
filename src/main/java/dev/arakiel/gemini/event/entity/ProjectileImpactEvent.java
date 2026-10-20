package dev.arakiel.gemini.event.entity;

import dev.arakiel.gemini.event.api.Cancelable;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;

@Cancelable
public class ProjectileImpactEvent extends EntityEvent {
    private final HitResult hitResult;

    public ProjectileImpactEvent(Projectile projectile, HitResult hitResult) {
        super(projectile);
        this.hitResult = Objects.requireNonNull(hitResult, "hitResult");
    }

    public Projectile getProjectile() {
        return (Projectile) getEntity();
    }

    public HitResult getHitResult() {
        return hitResult;
    }
}

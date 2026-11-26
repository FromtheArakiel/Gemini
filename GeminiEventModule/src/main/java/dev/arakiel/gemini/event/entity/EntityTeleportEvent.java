package dev.arakiel.gemini.event.entity;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

@Cancelable
public class EntityTeleportEvent extends EntityEvent {
    private Vec3 target;

    public EntityTeleportEvent(Entity entity, Vec3 target) {
        super(entity);
        this.target = Objects.requireNonNull(target, "target");
    }

    public Vec3 getTarget() {
        return this.target;
    }

    public double getTargetX() {
        return this.target.x;
    }

    public double getTargetY() {
        return this.target.y;
    }

    public double getTargetZ() {
        return this.target.z;
    }

    public void setTarget(double targetX, double targetY, double targetZ) {
        this.target = new Vec3(targetX, targetY, targetZ);
    }

    public void setTarget(Vec3 target) {
        this.target = Objects.requireNonNull(target, "target");
    }
}

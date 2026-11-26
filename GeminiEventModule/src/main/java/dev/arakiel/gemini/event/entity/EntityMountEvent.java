package dev.arakiel.gemini.event.entity;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

@Cancelable
public class EntityMountEvent extends EntityEvent {
    private final Entity vehicle;

    public EntityMountEvent(Entity rider, Entity vehicle) {
        super(rider);
        this.vehicle = Objects.requireNonNull(vehicle, "vehicle");
    }

    public Entity getRider() {
        return this.getEntity();
    }

    public Entity getVehicle() {
        return this.vehicle;
    }
}

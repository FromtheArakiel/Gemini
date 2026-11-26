package dev.arakiel.gemini.event.entity;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Objects;

@Cancelable
public class EntityTravelToDimensionEvent extends EntityEvent {
    private final ResourceKey<Level> dimension;

    public EntityTravelToDimensionEvent(Entity entity, ResourceKey<Level> dimension) {
        super(entity);
        this.dimension = Objects.requireNonNull(dimension, "dimension");
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }
}

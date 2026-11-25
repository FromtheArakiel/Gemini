package dev.arakiel.gemini.event.entity;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class EntityEvent extends GeminiEvent {
    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = Objects.requireNonNull(entity, "entity");
    }

    public Entity getEntity() {
        return entity;
    }
}

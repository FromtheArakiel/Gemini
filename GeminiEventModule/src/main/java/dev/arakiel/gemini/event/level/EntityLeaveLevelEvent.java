package dev.arakiel.gemini.event.level;

import dev.arakiel.gemini.event.entity.EntityEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class EntityLeaveLevelEvent extends EntityEvent {
    private final ServerLevel level;

    public EntityLeaveLevelEvent(Entity entity, ServerLevel level) {
        super(entity);
        this.level = Objects.requireNonNull(level, "level");
    }

    public ServerLevel getLevel() {
        return level;
    }
}

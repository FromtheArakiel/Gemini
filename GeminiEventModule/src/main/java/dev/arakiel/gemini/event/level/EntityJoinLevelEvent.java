package dev.arakiel.gemini.event.level;

import dev.arakiel.gemini.event.entity.EntityEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class EntityJoinLevelEvent extends EntityEvent {
    private final ServerLevel level;

    public EntityJoinLevelEvent(Entity entity, ServerLevel level) {
        super(entity);
        this.level = Objects.requireNonNull(level, "level");
    }

    public ServerLevel getLevel() {
        return level;
    }
}

package dev.arakiel.gemini.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;

public class EntityMobGriefingEvent extends EntityEvent {
    private final boolean mobGriefingEnabled;
    private boolean canGrief;

    public EntityMobGriefingEvent(Entity entity) {
        this(entity.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING), entity);
    }

    public EntityMobGriefingEvent(boolean mobGriefingEnabled, Entity entity) {
        super(entity);
        this.mobGriefingEnabled = mobGriefingEnabled;
        this.canGrief = mobGriefingEnabled;
    }

    public boolean isMobGriefingEnabled() {
        return this.mobGriefingEnabled;
    }

    public boolean canGrief() {
        return this.canGrief;
    }

    public void setCanGrief(boolean canGrief) {
        this.canGrief = canGrief;
    }
}

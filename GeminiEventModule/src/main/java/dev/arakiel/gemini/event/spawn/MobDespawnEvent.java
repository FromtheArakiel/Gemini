package dev.arakiel.gemini.event.spawn;

import dev.arakiel.gemini.event.entity.EntityEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;

import java.util.Objects;

public class MobDespawnEvent extends EntityEvent {
    private final RemovalReason reason;
    private Result result = Result.DEFAULT;

    public MobDespawnEvent(Mob mob, RemovalReason reason) {
        super(mob);
        this.reason = Objects.requireNonNull(reason, "reason");
    }

    public MobDespawnEvent(Mob mob) {
        this(mob, RemovalReason.DISCARDED);
    }

    public Mob getMob() {
        return (Mob) this.getEntity();
    }

    public RemovalReason getReason() {
        return this.reason;
    }

    public Result getDespawnResult() {
        return this.result;
    }

    public void setDespawnResult(Result result) {
        this.result = Objects.requireNonNull(result, "result");
    }

    public enum Result {
        ALLOW,
        DEFAULT,
        DENY
    }
}

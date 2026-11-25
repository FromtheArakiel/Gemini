package dev.arakiel.gemini.event.spawn;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.EntityEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;

import java.util.Objects;

@Cancelable
public class MobDespawnEvent extends EntityEvent {
    private final RemovalReason reason;

    public MobDespawnEvent(Mob mob, RemovalReason reason) {
        super(mob);
        this.reason = Objects.requireNonNull(reason, "reason");
    }

    public Mob getMob() {
        return (Mob) this.getEntity();
    }

    public RemovalReason getReason() {
        return this.reason;
    }
}

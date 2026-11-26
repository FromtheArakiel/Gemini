package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.EntityEvent;
import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.Objects;

@Cancelable
public class MobSplitEvent extends EntityEvent {
    private final List<Mob> children;

    public MobSplitEvent(Mob parent, List<Mob> children) {
        super(parent);
        this.children = Objects.requireNonNull(children, "children");
    }

    public Mob getParent() {
        return (Mob) this.getEntity();
    }

    public List<Mob> getChildren() {
        return this.children;
    }
}

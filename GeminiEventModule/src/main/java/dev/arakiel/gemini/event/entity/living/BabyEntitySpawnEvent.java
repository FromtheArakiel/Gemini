package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@Cancelable
public class BabyEntitySpawnEvent extends LivingEvent {
    private final LivingEntity partner;
    private AgeableMob child;

    public BabyEntitySpawnEvent(LivingEntity parent, LivingEntity partner, @Nullable AgeableMob child) {
        super(parent);
        this.partner = partner;
        this.child = child;
    }

    public LivingEntity getPartner() {
        return this.partner;
    }

    public @Nullable AgeableMob getChild() {
        return this.child;
    }

    public void setChild(@Nullable AgeableMob child) {
        this.child = child;
    }
}

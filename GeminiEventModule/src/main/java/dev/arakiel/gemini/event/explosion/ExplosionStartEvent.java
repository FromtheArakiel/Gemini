package dev.arakiel.gemini.event.explosion;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.level.Explosion;

import java.util.Objects;

@Cancelable
public class ExplosionStartEvent extends GeminiEvent {
    private final Explosion explosion;

    public ExplosionStartEvent(Explosion explosion) {
        this.explosion = Objects.requireNonNull(explosion, "explosion");
    }

    public Explosion getExplosion() {
        return this.explosion;
    }
}

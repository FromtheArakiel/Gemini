package dev.arakiel.gemini.event.combat;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class CriticalHitEvent extends GeminiEvent {
    private final Player player;
    private final Entity target;
    private final float vanillaMultiplier;
    private final boolean vanillaCritical;
    private float damageMultiplier;
    private boolean criticalHit;
    private boolean disableSweep = true;

    public CriticalHitEvent(Player player, Entity target, float damageMultiplier, boolean criticalHit) {
        this.player = Objects.requireNonNull(player, "player");
        this.target = Objects.requireNonNull(target, "target");
        this.vanillaMultiplier = validMultiplier(damageMultiplier);
        this.damageMultiplier = this.vanillaMultiplier;
        this.vanillaCritical = criticalHit;
        this.criticalHit = criticalHit;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Entity getTarget() {
        return this.target;
    }

    public boolean isCriticalHit() {
        return this.criticalHit;
    }

    public void setCriticalHit(boolean criticalHit) {
        this.criticalHit = criticalHit;
    }

    public float getDamageMultiplier() {
        return this.damageMultiplier;
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = validMultiplier(damageMultiplier);
    }

    public float getVanillaMultiplier() {
        return this.vanillaMultiplier;
    }

    public boolean isVanillaCritical() {
        return this.vanillaCritical;
    }

    public void setDisableSweep(boolean disableSweep) {
        this.disableSweep = disableSweep;
    }

    public boolean disableSweep() {
        return this.disableSweep;
    }

    private static float validMultiplier(float multiplier) {
        if (multiplier < 0.0F) {
            throw new IllegalArgumentException("Damage multiplier cannot be negative: " + multiplier);
        }
        return multiplier;
    }
}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.explosion.ExplosionDetonateEvent;
import dev.arakiel.gemini.event.explosion.ExplosionKnockbackEvent;
import dev.arakiel.gemini.event.explosion.ExplosionStartEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(Explosion.class)
public abstract class ExplosionEventMixin {
    @Shadow
    @Final
    private Level level;

    @Shadow
    @Final
    private Entity source;

    @Shadow
    @Final
    private float radius;

    @Shadow
    public abstract Vec3 center();

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void gemini$explosionStart(CallbackInfo callback) {
        ExplosionStartEvent event = new ExplosionStartEvent((Explosion) (Object) this);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "finalizeExplosion", at = @At("HEAD"), cancellable = true)
    private void gemini$explosionDetonate(boolean spawnParticles, CallbackInfo callback) {
        Explosion explosion = (Explosion) (Object) this;
        AABB area = new AABB(this.center(), this.center()).inflate(this.radius * 2.0D + 1.0D);
        var entities = new ArrayList<>(this.level.getEntities(
                this.source,
                area,
                entity -> entity instanceof LivingEntity && entity.isAlive()
        ));
        ExplosionDetonateEvent event = new ExplosionDetonateEvent(explosion, entities);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "getHitPlayers", at = @At("RETURN"), cancellable = true)
    private void gemini$explosionKnockback(CallbackInfoReturnable<Map<Player, Vec3>> callback) {
        Explosion explosion = (Explosion) (Object) this;
        Map<Player, Vec3> knockback = new LinkedHashMap<>(callback.getReturnValue());
        knockback.entrySet().removeIf(entry -> {
            ExplosionKnockbackEvent event = new ExplosionKnockbackEvent(explosion, entry.getKey(), entry.getValue());
            GeminiEvents.post(event);
            if (event.isCanceled()) {
                return true;
            }
            entry.setValue(event.getKnockback());
            return false;
        });
        callback.setReturnValue(Map.copyOf(knockback));
    }
}

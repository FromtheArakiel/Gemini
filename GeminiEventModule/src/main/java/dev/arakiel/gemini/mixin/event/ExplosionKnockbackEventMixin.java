package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.explosion.ExplosionKnockbackEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(Explosion.class)
public abstract class ExplosionKnockbackEventMixin {
    @Unique
    private final Map<Entity, Vec3> gemini$knockbackByEntity = new IdentityHashMap<>();

    @Redirect(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
            )
    )
    private void gemini$explosionKnockback(Entity entity, Vec3 movement) {
        Vec3 knockback = movement.subtract(entity.getDeltaMovement());
        ExplosionKnockbackEvent event = new ExplosionKnockbackEvent(
                (Explosion) (Object) this,
                entity,
                knockback
        );
        GeminiEvents.post(event);
        this.gemini$knockbackByEntity.put(entity, event.getKnockback());
        entity.setDeltaMovement(entity.getDeltaMovement().add(event.getKnockback()));
    }

    @Inject(method = "getHitPlayers", at = @At("RETURN"), cancellable = true)
    private void gemini$hitPlayers(CallbackInfoReturnable<Map<Player, Vec3>> callback) {
        Map<Player, Vec3> result = new LinkedHashMap<>(callback.getReturnValue());
        result.replaceAll((player, original) -> this.gemini$knockbackByEntity.getOrDefault(player, original));
        callback.setReturnValue(Map.copyOf(result));
    }
}

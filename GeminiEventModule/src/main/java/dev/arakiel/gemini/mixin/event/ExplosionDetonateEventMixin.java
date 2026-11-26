package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.explosion.ExplosionDetonateEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(Explosion.class)
public abstract class ExplosionDetonateEventMixin {
    @ModifyVariable(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;",
                    shift = At.Shift.AFTER
            ),
            ordinal = 0
    )
    private List<Entity> gemini$explosionDetonate(List<Entity> entities) {
        ExplosionDetonateEvent event = new ExplosionDetonateEvent(
                (Explosion) (Object) this,
                entities
        );
        GeminiEvents.post(event);
        return event.getAffectedEntities();
    }
}

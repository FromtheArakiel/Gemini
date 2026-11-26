package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.EntityTeleportEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityTeleportEventMixin {
    @Unique
    private boolean gemini$applyingTeleport;

    @Inject(
            method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gemini$teleport(
            ServerLevel level,
            double x,
            double y,
            double z,
            Set<RelativeMovement> relativeMovements,
            float yaw,
            float pitch,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (this.gemini$applyingTeleport) {
            return;
        }
        EntityTeleportEvent event = new EntityTeleportEvent((Entity) (Object) this, new Vec3(x, y, z));
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
            return;
        }
        Vec3 target = event.getTarget();
        if (target.x != x || target.y != y || target.z != z) {
            this.gemini$applyingTeleport = true;
            try {
                callback.setReturnValue(((Entity) (Object) this).teleportTo(
                        level,
                        target.x,
                        target.y,
                        target.z,
                        relativeMovements,
                        yaw,
                        pitch
                ));
            } finally {
                this.gemini$applyingTeleport = false;
            }
        }
    }
}

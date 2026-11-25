package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.ProjectileImpactEvent;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {
    @Inject(method = "hitTargetOrDeflectSelf", at = @At("HEAD"), cancellable = true)
    private void gemini$projectileImpact(HitResult hitResult, CallbackInfoReturnable<ProjectileDeflection> callback) {
        ProjectileImpactEvent event = new ProjectileImpactEvent((Projectile) (Object) this, hitResult);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(ProjectileDeflection.NONE);
        }
    }
}

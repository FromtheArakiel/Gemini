package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.spawn.MobDespawnEvent;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobDespawnEventMixin {
    @Unique
    private MobDespawnEvent gemini$despawnEvent;

    @Inject(method = "checkDespawn", at = @At("HEAD"))
    private void gemini$postDespawn(CallbackInfo callback) {
        this.gemini$despawnEvent = new MobDespawnEvent((Mob) (Object) this);
        GeminiEvents.post(this.gemini$despawnEvent);
    }

    @Redirect(
            method = "checkDespawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Mob;discard()V"
            )
    )
    private void gemini$allowDiscard(Mob mob) {
        if (this.gemini$despawnEvent == null
                || this.gemini$despawnEvent.getDespawnResult() != MobDespawnEvent.Result.DENY) {
            mob.discard();
        }
    }

    @Inject(method = "checkDespawn", at = @At("RETURN"))
    private void gemini$finishDespawn(CallbackInfo callback) {
        if (this.gemini$despawnEvent != null
                && this.gemini$despawnEvent.getDespawnResult() == MobDespawnEvent.Result.ALLOW
                && !((Mob) (Object) this).isRemoved()) {
            ((Mob) (Object) this).discard();
        }
        this.gemini$despawnEvent = null;
    }
}

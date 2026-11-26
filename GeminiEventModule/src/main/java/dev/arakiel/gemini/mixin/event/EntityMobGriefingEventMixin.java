package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.EntityMobGriefingEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RemoveBlockGoal.class)
public abstract class EntityMobGriefingEventMixin {
    @Shadow
    @Final
    private Mob removerMob;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void gemini$mobGriefing(CallbackInfoReturnable<Boolean> callback) {
        if (this.removerMob == null) {
            return;
        }
        EntityMobGriefingEvent event = new EntityMobGriefingEvent(this.removerMob);
        GeminiEvents.post(event);
        if (!event.canGrief()) {
            callback.setReturnValue(false);
        }
    }
}

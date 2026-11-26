package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.EntityMobGriefingEvent;
import net.minecraft.world.entity.monster.Silverfish;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.Silverfish$SilverfishWakeUpFriendsGoal")
public abstract class EntityMobGriefingEventSilverfishWakeUpMixin {
    @Shadow
    @Final
    private Silverfish silverfish;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void gemini$mobGriefing(CallbackInfoReturnable<Boolean> callback) {
        EntityMobGriefingEvent event = new EntityMobGriefingEvent(this.silverfish);
        GeminiEvents.post(event);
        if (!event.canGrief()) {
            callback.setReturnValue(false);
        }
    }
}

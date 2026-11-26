package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.EntityMobGriefingEvent;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanLeaveBlockGoal")
public abstract class EntityMobGriefingEventEnderManLeaveMixin {
    @Shadow
    @Final
    private EnderMan enderman;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void gemini$mobGriefing(CallbackInfoReturnable<Boolean> callback) {
        EntityMobGriefingEvent event = new EntityMobGriefingEvent(this.enderman);
        GeminiEvents.post(event);
        if (!event.canGrief()) {
            callback.setReturnValue(false);
        }
    }
}

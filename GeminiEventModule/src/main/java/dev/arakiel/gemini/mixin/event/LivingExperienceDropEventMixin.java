package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingExperienceDropEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingExperienceDropEventMixin {
    @Redirect(
            method = "dropExperience",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
            )
    )
    private void gemini$experienceDrop(ServerLevel level, Vec3 pos, int amount) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Player attackingPlayer = entity.getKillCredit() instanceof Player player ? player : null;
        LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, amount);
        GeminiEvents.post(event);
        if (!event.isCanceled()) {
            ExperienceOrb.award(level, pos, event.getDroppedExperience());
        }
    }
}

package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.api.GeminiEvents;
import dev.arakiel.gemini.event.entity.player.PlayerXpPickupEvent;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void gemini$xpPickup(Player player, CallbackInfo callback) {
        ExperienceOrb orb = (ExperienceOrb) (Object) this;
        PlayerXpPickupEvent event = new PlayerXpPickupEvent(player, orb, orb.getValue());
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

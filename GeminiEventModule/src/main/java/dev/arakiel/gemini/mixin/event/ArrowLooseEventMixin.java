package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.projectile.ArrowLooseEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public abstract class ArrowLooseEventMixin {
    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void gemini$arrowLoose(
            ItemStack bow,
            Level level,
            LivingEntity entity,
            int remainingUseTicks,
            CallbackInfo callback
    ) {
        if (!(entity instanceof Player player)) {
            return;
        }
        int charge = bow.getUseDuration(entity) - remainingUseTicks;
        ArrowLooseEvent event = new ArrowLooseEvent(player, bow, charge);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

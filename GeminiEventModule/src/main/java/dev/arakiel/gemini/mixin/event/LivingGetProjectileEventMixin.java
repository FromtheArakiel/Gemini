package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingGetProjectileEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class, Player.class})
public abstract class LivingGetProjectileEventMixin {
    @Inject(method = "getProjectile", at = @At("RETURN"), cancellable = true)
    private void gemini$getProjectile(
            ItemStack weapon,
            CallbackInfoReturnable<ItemStack> callback
    ) {
        LivingGetProjectileEvent event = new LivingGetProjectileEvent(
                (LivingEntity) (Object) this,
                weapon,
                callback.getReturnValue()
        );
        GeminiEvents.post(event);
        callback.setReturnValue(event.isCanceled()
                ? ItemStack.EMPTY
                : event.getProjectile());
    }
}

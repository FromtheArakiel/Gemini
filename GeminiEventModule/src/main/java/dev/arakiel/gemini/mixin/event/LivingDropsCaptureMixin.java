package dev.arakiel.gemini.mixin.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import dev.arakiel.gemini.event.internal.GeminiDropCapture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class LivingDropsCaptureMixin {
    @Inject(
            method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gemini$captureDeathDrop(ItemStack stack, CallbackInfoReturnable<ItemEntity> callback) {
        if (GeminiDropCapture.capture(stack)) {
            callback.setReturnValue(null);
        }
    }
}

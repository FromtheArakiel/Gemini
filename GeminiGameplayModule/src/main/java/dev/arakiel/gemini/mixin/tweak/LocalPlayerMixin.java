package dev.arakiel.gemini.mixin.tweak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.arakiel.gemini.tweak.FastUseItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow
    private boolean startedUsingItem;

    @WrapOperation(
            method = {"aiStep", "canStartSprinting"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z")
    )
    private boolean gemini$fastUseItem(LocalPlayer player, Operation<Boolean> original) {
        if (startedUsingItem) {
            ItemStack stack = player.getUseItem();
            if (FastUseItem.matches(stack)) {
                return false;
            }
        }
        return original.call(player);
    }
}

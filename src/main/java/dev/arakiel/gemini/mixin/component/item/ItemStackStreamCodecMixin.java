package dev.arakiel.gemini.mixin.component.item;

import dev.arakiel.gemini.component.internal.itemstack.GeminiItemStackInternals;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.item.ItemStack$1")
public abstract class ItemStackStreamCodecMixin {
    @Inject(
            method = "encode(Lnet/minecraft/network/RegistryFriendlyByteBuf;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD")
    )
    private void gemini$commitBeforeNetworkEncode(
            RegistryFriendlyByteBuf buffer,
            ItemStack stack,
            CallbackInfo ci
    ) {
        GeminiItemStackInternals.commit(stack, buffer.registryAccess());
    }
}

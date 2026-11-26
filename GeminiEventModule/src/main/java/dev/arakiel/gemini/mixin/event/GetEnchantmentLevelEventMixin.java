package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.enchant.GetEnchantmentLevelEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EnchantmentHelper.class)
public abstract class GetEnchantmentLevelEventMixin {
    @Inject(method = "getItemEnchantmentLevel", at = @At("RETURN"), cancellable = true)
    private static void gemini$getEnchantmentLevel(
            Holder<Enchantment> enchantment,
            ItemStack stack,
            CallbackInfoReturnable<Integer> callback
    ) {
        GetEnchantmentLevelEvent event = new GetEnchantmentLevelEvent(
                stack,
                Map.of(enchantment, callback.getReturnValue())
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(0);
        } else {
            callback.setReturnValue(event.getLevels().getOrDefault(enchantment, 0));
        }
    }
}

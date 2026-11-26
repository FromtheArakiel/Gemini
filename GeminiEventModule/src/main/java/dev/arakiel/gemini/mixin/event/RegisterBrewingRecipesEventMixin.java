package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.brewing.RegisterBrewingRecipesEvent;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PotionBrewing.class)
public abstract class RegisterBrewingRecipesEventMixin {
    @Inject(
            method = "bootstrap(Lnet/minecraft/world/flag/FeatureFlagSet;)Lnet/minecraft/world/item/alchemy/PotionBrewing;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/alchemy/PotionBrewing;addVanillaMixes(Lnet/minecraft/world/item/alchemy/PotionBrewing$Builder;)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void gemini$registerBrewingRecipes(
            FeatureFlagSet flags,
            CallbackInfoReturnable<PotionBrewing> callback,
            PotionBrewing.Builder builder
    ) {
        GeminiEvents.post(new RegisterBrewingRecipesEvent(builder));
    }
}

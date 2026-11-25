package dev.arakiel.gemini.mixin.attribute;

import dev.arakiel.gemini.attribute.GeminiAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerAttributeMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void gemini$addPlayerAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> callback) {
        GeminiAttributes.addPlayerAttributes(callback.getReturnValue());
    }
}

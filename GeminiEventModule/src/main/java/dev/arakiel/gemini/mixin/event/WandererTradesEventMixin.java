package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.internal.GeminiTradeRuntime;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public abstract class WandererTradesEventMixin {
    @Inject(method = "updateTrades", at = @At("HEAD"))
    private void gemini$wandererTrades(CallbackInfo callback) {
        GeminiTradeRuntime.applyWanderer();
    }
}

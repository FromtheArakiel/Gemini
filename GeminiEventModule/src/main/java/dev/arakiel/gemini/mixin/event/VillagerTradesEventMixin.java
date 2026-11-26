package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.internal.GeminiTradeRuntime;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerTradesEventMixin {
    @Inject(method = "updateTrades", at = @At("HEAD"))
    private void gemini$villagerTrades(CallbackInfo callback) {
        Villager villager = (Villager) (Object) this;
        GeminiTradeRuntime.applyVillager(villager.getVillagerData().getProfession());
    }
}

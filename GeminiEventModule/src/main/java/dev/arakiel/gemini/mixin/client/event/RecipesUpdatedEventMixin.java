package dev.arakiel.gemini.mixin.client.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.RecipesUpdatedEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class RecipesUpdatedEventMixin {
    @Inject(method = "handleUpdateRecipes", at = @At("RETURN"))
    private void gemini$recipesUpdated(ClientboundUpdateRecipesPacket packet, CallbackInfo callback) {
        ClientPacketListener listener = (ClientPacketListener) (Object) this;
        GeminiEvents.post(new RecipesUpdatedEvent(listener.getRecipeManager()));
    }
}

package dev.arakiel.gemini.mixin.client.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.TextureAtlasStitchedEvent;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasStitchedEventMixin {
    @Inject(method = "upload", at = @At("HEAD"))
    private void gemini$atlasStitched(SpriteLoader.Preparations preparations, CallbackInfo callback) {
        GeminiEvents.post(new TextureAtlasStitchedEvent((TextureAtlas) (Object) this));
    }
}

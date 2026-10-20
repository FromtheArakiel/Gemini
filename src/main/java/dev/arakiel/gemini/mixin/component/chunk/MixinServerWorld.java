package dev.arakiel.gemini.mixin.component.chunk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerLevel.class})
public abstract class MixinServerWorld {
   @Inject(
      method = {"tickChunk"},
      at = {@At("RETURN")}
   )
   private void tick(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
      chunk.asComponentProvider().getComponentContainer().tickServerComponents();
   }
}

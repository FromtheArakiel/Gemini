package dev.arakiel.gemini.mixin.component.chunk;

import dev.arakiel.gemini.component.api.ComponentCallbacks;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChunkHolder.class})
public abstract class MixinChunkHolder {
   @Inject(
      method = {"broadcastChanges"},
      at = {@At(
         value = "FIELD",
         opcode = 181,
         target = "Lnet/minecraft/server/level/ChunkHolder;hasChangedSections:Z",
         ordinal = 0,
         shift = Shift.AFTER
      )}
   )
   private void onPlayerLogIn(LevelChunk chunk, CallbackInfo ci) {
      for (ServerPlayer p : PlayerLookup.tracking((ServerLevel)chunk.getLevel(), chunk.getPos())) {
         ((ComponentCallbacks.ChunkSync)ComponentCallbacks.ChunkSync.EVENT.invoker()).onChunkSync(p, chunk);
      }
   }
}

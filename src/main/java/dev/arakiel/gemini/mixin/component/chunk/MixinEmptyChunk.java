package dev.arakiel.gemini.mixin.component.chunk;

import dev.arakiel.gemini.component.api.ComponentContainer;
import dev.arakiel.gemini.component.api.ComponentProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EmptyLevelChunk.class})
public abstract class MixinEmptyChunk extends LevelChunk implements ComponentProvider {
   public MixinEmptyChunk(Level world, ChunkPos pos) {
      super(world, pos);
   }

   @NotNull
   @Override
   public ComponentContainer getComponentContainer() {
      return ComponentContainer.EMPTY;
   }
}

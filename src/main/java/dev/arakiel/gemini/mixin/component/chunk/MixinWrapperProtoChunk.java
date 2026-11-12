package dev.arakiel.gemini.mixin.component.chunk;

import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentProvider;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ImposterProtoChunk.class})
public abstract class MixinWrapperProtoChunk implements ComponentProvider {
   @Shadow
   @Final
   private LevelChunk wrapped;

   @Override
   public ComponentContainer gemini$getComponentContainer() {
      return this.wrapped.asComponentProvider().gemini$getComponentContainer();
   }
}

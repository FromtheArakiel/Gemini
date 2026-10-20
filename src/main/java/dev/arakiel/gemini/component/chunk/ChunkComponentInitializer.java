package dev.arakiel.gemini.component.chunk;

import dev.arakiel.gemini.component.internal.base.ComponentRegistrationInitializer;

public interface ChunkComponentInitializer extends ComponentRegistrationInitializer {
   void registerChunkComponentFactories(ChunkComponentFactoryRegistry var1);
}

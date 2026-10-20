package dev.arakiel.gemini.component.block;

import dev.arakiel.gemini.component.internal.base.ComponentRegistrationInitializer;

public interface BlockComponentInitializer extends ComponentRegistrationInitializer {
   void registerBlockComponentFactories(BlockComponentFactoryRegistry var1);
}

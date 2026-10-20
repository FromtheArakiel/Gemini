package dev.arakiel.gemini.component.world;

import dev.arakiel.gemini.component.internal.base.ComponentRegistrationInitializer;

public interface WorldComponentInitializer extends ComponentRegistrationInitializer {
   void registerWorldComponentFactories(WorldComponentFactoryRegistry var1);
}

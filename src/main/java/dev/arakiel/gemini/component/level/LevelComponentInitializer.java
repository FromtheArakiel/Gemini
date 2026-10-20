package dev.arakiel.gemini.component.level;

import dev.arakiel.gemini.component.internal.base.ComponentRegistrationInitializer;

public interface LevelComponentInitializer extends ComponentRegistrationInitializer {
   void registerLevelComponentFactories(LevelComponentFactoryRegistry var1);
}

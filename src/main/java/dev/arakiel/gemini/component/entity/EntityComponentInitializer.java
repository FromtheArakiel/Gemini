package dev.arakiel.gemini.component.entity;

import dev.arakiel.gemini.component.internal.base.ComponentRegistrationInitializer;

public interface EntityComponentInitializer extends ComponentRegistrationInitializer {
   void registerEntityComponentFactories(EntityComponentFactoryRegistry var1);
}

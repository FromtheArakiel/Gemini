package dev.arakiel.gemini.component.item;

import dev.arakiel.gemini.component.internal.base.ComponentRegistrationInitializer;

public interface ItemComponentInitializer extends ComponentRegistrationInitializer {
   void registerItemComponentMigrations(ItemComponentMigrationRegistry var1);
}

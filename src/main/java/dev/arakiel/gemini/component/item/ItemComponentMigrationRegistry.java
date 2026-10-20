package dev.arakiel.gemini.component.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

public interface ItemComponentMigrationRegistry {
   void registerMigration(ResourceLocation var1, DataComponentType<?> var2);
}

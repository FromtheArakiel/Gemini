package dev.arakiel.gemini.component.api;

import java.util.Collection;
import java.util.Collections;
import net.minecraft.resources.ResourceLocation;

public interface StaticComponentInitializer {
   default Collection<ResourceLocation> getSupportedComponentKeys() {
      return Collections.emptySet();
   }

   default void finalizeStaticBootstrap() {
   }
}

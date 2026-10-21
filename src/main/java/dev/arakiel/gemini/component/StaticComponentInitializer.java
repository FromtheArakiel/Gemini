package dev.arakiel.gemini.component;

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

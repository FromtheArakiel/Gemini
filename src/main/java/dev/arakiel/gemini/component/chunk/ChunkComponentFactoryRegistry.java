package dev.arakiel.gemini.component.chunk;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface ChunkComponentFactoryRegistry {
   <C extends Component> void register(ComponentKey<C> var1, ComponentFactory<ChunkAccess, ? extends C> var2);

   <C extends Component> void register(ComponentKey<? super C> var1, Class<C> var2, ComponentFactory<ChunkAccess, ? extends C> var3);
}

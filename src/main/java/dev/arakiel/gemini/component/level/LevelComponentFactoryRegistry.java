package dev.arakiel.gemini.component.level;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import net.minecraft.world.level.storage.LevelData;

public interface LevelComponentFactoryRegistry {
   <C extends Component> void register(ComponentKey<C> var1, ComponentFactory<LevelData, ? extends C> var2);

   <C extends Component> void register(ComponentKey<? super C> var1, Class<C> var2, ComponentFactory<LevelData, ? extends C> var3);
}

package dev.arakiel.gemini.component.world;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface WorldComponentFactoryRegistry {
   <C extends Component> void register(ComponentKey<C> var1, ComponentFactory<Level, ? extends C> var2);

   <C extends Component> void register(ComponentKey<? super C> var1, Class<C> var2, ComponentFactory<Level, ? extends C> var3);

   <C extends Component> void registerFor(ResourceKey<Level> var1, ComponentKey<C> var2, ComponentFactory<Level, ? extends C> var3);

   <C extends Component> void registerFor(ResourceKey<Level> var1, ComponentKey<? super C> var2, Class<C> var3, ComponentFactory<Level, ? extends C> var4);
}

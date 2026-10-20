package dev.arakiel.gemini.component.entity;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus.Experimental;

public interface EntityComponentFactoryRegistry {
   <C extends Component, E extends Entity> void registerFor(Class<E> var1, ComponentKey<C> var2, ComponentFactory<E, ? extends C> var3);

   @Deprecated
   <C extends Component> void registerFor(Predicate<Class<? extends Entity>> var1, ComponentKey<C> var2, ComponentFactory<Entity, C> var3);

   <C extends Component, E extends Entity> EntityComponentFactoryRegistry.Registration<C, E> beginRegistration(Class<E> var1, ComponentKey<C> var2);

   <C extends RespawnableComponent<? super C>> void registerForPlayers(ComponentKey<? super C> var1, ComponentFactory<Player, C> var2);

   <C extends Component, P extends C> void registerForPlayers(ComponentKey<C> var1, ComponentFactory<Player, P> var2, RespawnCopyStrategy<? super P> var3);

   public interface Registration<C extends Component, E extends Entity> {
      EntityComponentFactoryRegistry.Registration<C, E> filter(Predicate<Class<? extends E>> var1);

      @Experimental
      EntityComponentFactoryRegistry.Registration<C, E> after(ComponentKey<?> var1);

      <I extends C> EntityComponentFactoryRegistry.Registration<I, E> impl(Class<I> var1);

      EntityComponentFactoryRegistry.Registration<C, E> respawnStrategy(RespawnCopyStrategy<? super C> var1);

      void end(ComponentFactory<E, C> var1);
   }
}

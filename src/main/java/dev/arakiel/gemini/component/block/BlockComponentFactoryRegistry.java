package dev.arakiel.gemini.component.block;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import java.util.function.Predicate;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus.Experimental;

public interface BlockComponentFactoryRegistry {
   <C extends Component, BE extends BlockEntity> void registerFor(Class<BE> var1, ComponentKey<C> var2, ComponentFactory<BE, C> var3);

   <C extends Component, B extends BlockEntity> BlockComponentFactoryRegistry.Registration<C, B> beginRegistration(Class<B> var1, ComponentKey<C> var2);

   public interface Registration<C extends Component, BE extends BlockEntity> {
      BlockComponentFactoryRegistry.Registration<C, BE> filter(Predicate<Class<? extends BE>> var1);

      @Experimental
      BlockComponentFactoryRegistry.Registration<C, BE> after(ComponentKey<?> var1);

      <I extends C> BlockComponentFactoryRegistry.Registration<I, BE> impl(Class<I> var1);

      void end(ComponentFactory<BE, C> var1);
   }
}

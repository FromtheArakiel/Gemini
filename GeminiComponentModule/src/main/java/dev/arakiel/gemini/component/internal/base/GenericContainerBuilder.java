package dev.arakiel.gemini.component.internal.base;

import com.google.common.base.Preconditions;
import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.internal.base.asm.ComponentAsmHelper;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentLoadingException;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.ApiStatus.Experimental;

@SuppressWarnings({"unchecked", "deprecation"})
public class GenericContainerBuilder<I, R> {
   private boolean built;
   private String factoryNameSuffix;
   private final Map<ComponentKey<?>, QualifiedComponentFactory<I>> factories = new LinkedHashMap<>();
   private final List<Class<?>> argClasses;
   private final R emptyFactory;
   private final Class<? super I> componentFactoryClass;
   private final Class<? super R> containerFactoryType;

   public GenericContainerBuilder(Class<? super I> componentFactoryClass, Class<? super R> containerFactoryType, List<Class<?>> argClasses, R emptyFactory) {
      this.argClasses = argClasses;
      this.emptyFactory = emptyFactory;
      this.componentFactoryClass = componentFactoryClass;
      this.containerFactoryType = containerFactoryType;
   }

   @Experimental
   public void checkDuplicate(ComponentKey<?> key, Function<I, String> msgFactory) {
      if (this.factories.containsKey(key)) {
         throw new StaticComponentLoadingException(msgFactory.apply(this.factories.get(key).factory()));
      }
   }

   public GenericContainerBuilder<I, R> factoryNameSuffix(String factoryNameSuffix) {
      Preconditions.checkState(this.factoryNameSuffix == null);
      this.factoryNameSuffix = factoryNameSuffix;
      return this;
   }

   @Contract(
      mutates = "this"
   )
   public <C extends Component> GenericContainerBuilder<I, R> component(
      ComponentKey<? super C> key, Class<C> implClass, I factory, Set<ComponentKey<?>> dependencies
   ) {
      this.addComponent(key, new QualifiedComponentFactory<>(factory, implClass, dependencies));
      return this;
   }

   protected <C extends Component> void addComponent(ComponentKey<? super C> key, QualifiedComponentFactory<I> value) {
      this.factories.put(key, value);
   }

   public R build() {
      if (this.built) {
         throw new IllegalStateException("Cannot build more than one container factory with the same builder");
      } else {
         try {
            this.built = true;
            if (this.factories.isEmpty()) {
               return this.emptyFactory;
            } else {
               Class<? extends ComponentContainer> containerClass = ComponentAsmHelper.spinComponentContainer(this.componentFactoryClass, this.factories);
               Class<? extends R> factoryClass = StaticComponentPluginBase.spinContainerFactory(this.containerFactoryType, containerClass, this.argClasses);
               return ComponentsInternals.createFactory((Class<R>)factoryClass);
            }
         } catch (IOException var3) {
            throw new UncheckedIOException(var3);
         }
      }
   }
}

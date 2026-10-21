package dev.arakiel.gemini.component.api;

import dev.arakiel.gemini.component.internal.base.GenericContainerBuilder;
import dev.arakiel.gemini.component.internal.base.asm.AsmGeneratedCallback;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import java.util.List;
import java.util.Set;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface ComponentContainer extends NbtSerializable {
   ComponentContainer EMPTY = StaticComponentPluginBase.createEmptyContainer();

   @Contract(
      pure = true
   )
   @Unmodifiable
   Set<ComponentKey<?>> keys();

   @Contract(
      pure = true
   )
   boolean hasComponents();

   @Contract(
      mutates = "this"
   )
   void copyFrom(ComponentContainer var1, Provider var2);

   @AsmGeneratedCallback(ComponentLifecycle.ServerTicking.class)
   void tickServerComponents();

   @AsmGeneratedCallback(ComponentLifecycle.ClientTicking.class)
   void tickClientComponents();

   @AsmGeneratedCallback(ComponentLifecycle.ServerLoad.class)
   @Experimental
   void onServerLoad();

   @AsmGeneratedCallback(ComponentLifecycle.ServerUnload.class)
   @Experimental
   void onServerUnload();

   @AsmGeneratedCallback(ComponentLifecycle.ClientLoad.class)
   @Experimental
   void onClientLoad();

   @AsmGeneratedCallback(ComponentLifecycle.ClientUnload.class)
   @Experimental
   void onClientUnload();

   @Nullable
   @Experimental
   default ComponentKey<?> getKey(Component component) {
      for (ComponentKey<?> key : this.keys()) {
         if (key.getInternal(this) == component) {
            return key;
         }
      }

      return null;
   }

   @Contract(
      mutates = "this"
   )
   @Override
   void fromTag(CompoundTag var1, Provider var2);

   @Contract(
      mutates = "param"
   )
   @Override
   CompoundTag toTag(CompoundTag var1, Provider var2);

   @NonExtendable
   public interface Factory<T> {
      @Contract(
         value = "-> new",
         pure = true
      )
      static ComponentContainer.Factory.Builder<Void> builder() {
         return new ComponentContainer.Factory.Builder<>(Void.class);
      }

      @Contract(
         value = "_ -> new",
         pure = true
      )
      static <T> ComponentContainer.Factory.Builder<T> builder(Class<T> singleArgClass) {
         return new ComponentContainer.Factory.Builder<>(singleArgClass);
      }

      @Contract("_ -> new")
      ComponentContainer createContainer(@Nullable T var1);

      public static final class Builder<T> extends GenericContainerBuilder<ComponentFactory<T, ?>, ComponentContainer.Factory<T>> {
         Builder(Class<T> argClass) {
            super(ComponentFactory.class, ComponentContainer.Factory.class, List.of(argClass), t -> ComponentContainer.EMPTY);
         }

         @Contract(
            mutates = "this"
         )
         public <C extends Component> ComponentContainer.Factory.Builder<T> component(ComponentKey<C> key, ComponentFactory<T, ? extends C> factory) {
            return this.component(key, key.getComponentClass(), factory);
         }

         @Contract(
            mutates = "this"
         )
         public <C extends Component> ComponentContainer.Factory.Builder<T> component(
            ComponentKey<? super C> key, Class<C> implClass, ComponentFactory<T, ? extends C> factory
         ) {
            return this.component(key, implClass, factory, Set.of());
         }

         @Experimental
         @Contract(
            mutates = "this"
         )
         public <C extends Component> ComponentContainer.Factory.Builder<T> component(
            ComponentKey<? super C> key, Class<C> implClass, ComponentFactory<T, ? extends C> factory, Set<ComponentKey<?>> dependencies
         ) {
            super.component(key, implClass, factory, dependencies);
            return this;
         }

         public ComponentContainer.Factory.Builder<T> factoryNameSuffix(String factoryNameSuffix) {
            super.factoryNameSuffix(factoryNameSuffix);
            return this;
         }

         public ComponentContainer.Factory<T> build() {
            return (ComponentContainer.Factory<T>)super.build();
         }

      }
   }
}
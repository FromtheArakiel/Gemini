package dev.arakiel.gemini.component.internal.base;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.internal.base.asm.ComponentBootstrap;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ComponentRegistryImpl {
   public static final ComponentRegistryImpl INSTANCE = new ComponentRegistryImpl();
   private final Map<ResourceLocation, ComponentKey<?>> keys = new HashMap<>();

   public synchronized <T extends Component> ComponentKey<T> getOrCreate(ResourceLocation componentId, Class<T> componentClass) {
      Preconditions.checkArgument(Component.class.isAssignableFrom(componentClass), "Component interface must extend " + Component.class.getCanonicalName());
      ComponentKey<T> existing = (ComponentKey<T>)this.get(componentId);
      if (existing != null) {
         if (existing.getComponentClass() != componentClass) {
            throw new IllegalStateException(
               "Registered component " + componentId + " twice with 2 different classes: " + existing.getComponentClass() + ", " + componentClass
            );
         } else {
            return existing;
         }
      } else {
         Class<? extends ComponentKey<?>> generated = ComponentBootstrap.INSTANCE.getGeneratedComponentTypeClass(componentId);
         if (generated == null) {
            throw new IllegalStateException(componentId + " was not registered through mod metadata or plugin");
         } else {
            ComponentKey<T> registered = this.instantiateStaticType(generated, componentId, componentClass);
            this.keys.put(componentId, registered);
            return registered;
         }
      }
   }

   private <T extends Component> ComponentKey<T> instantiateStaticType(
      Class<? extends ComponentKey<?>> generated, ResourceLocation componentId, Class<T> componentClass
   ) {
      try {
         return (ComponentKey<T>)generated.getConstructor(ResourceLocation.class, Class.class).newInstance(componentId, componentClass);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var5) {
         throw new IllegalStateException("Failed to create statically declared component type", var5);
      }
   }

   @Nullable
   public ComponentKey<?> get(ResourceLocation id) {
      return this.keys.get(id);
   }

   public Stream<ComponentKey<?>> stream() {
      return new HashSet<>(this.keys.values()).stream();
   }

   @VisibleForTesting
   void clear(ResourceLocation id) {
      this.keys.remove(id);
   }
}

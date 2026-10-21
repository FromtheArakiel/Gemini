package dev.arakiel.gemini.component.api;

import dev.arakiel.gemini.component.internal.base.ComponentRegistryImpl;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public final class ComponentRegistry {
   public static <C extends Component> ComponentKey<C> getOrCreate(ResourceLocation componentId, Class<C> componentClass) {
      return ComponentRegistryImpl.INSTANCE.getOrCreate(componentId, componentClass);
   }

   @Nullable
   @Contract(
      pure = true
   )
   public static ComponentKey<?> get(ResourceLocation id) {
      return ComponentRegistryImpl.INSTANCE.get(id);
   }

   @Contract(
      pure = true
   )
   public static Stream<ComponentKey<?>> stream() {
      return ComponentRegistryImpl.INSTANCE.stream();
   }
}

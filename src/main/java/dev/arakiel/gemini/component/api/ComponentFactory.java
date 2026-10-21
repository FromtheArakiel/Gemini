package dev.arakiel.gemini.component.api;

import org.jetbrains.annotations.Contract;

@FunctionalInterface
public interface ComponentFactory<T, C extends Component> {
   @Contract(
      value = "_ -> new",
      pure = true
   )
   C createComponent(T var1);
}

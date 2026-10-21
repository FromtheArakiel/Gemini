package dev.arakiel.gemini.component.internal.base.asm;

import dev.arakiel.gemini.component.api.Component;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AsmGeneratedCallback {
   Class<? extends Component> value();
}

package dev.arakiel.gemini.component.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.fabricmc.api.EnvType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface CheckEnvironment {
   EnvType value();
}

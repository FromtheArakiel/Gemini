package dev.arakiel.gemini.component.internal.base.asm;

public class StaticComponentLoadingException extends RuntimeException {
   public StaticComponentLoadingException() {
   }

   public StaticComponentLoadingException(String message) {
      super(message);
   }

   public StaticComponentLoadingException(String message, Throwable cause) {
      super(message, cause);
   }
}

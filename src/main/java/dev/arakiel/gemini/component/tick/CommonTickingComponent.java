package dev.arakiel.gemini.component.tick;

public interface CommonTickingComponent extends ServerTickingComponent, ClientTickingComponent {
   @Override
   default void clientTick() {
      this.tick();
   }

   @Override
   default void serverTick() {
      this.tick();
   }

   void tick();
}

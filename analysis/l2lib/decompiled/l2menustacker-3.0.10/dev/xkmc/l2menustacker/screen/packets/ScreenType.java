package dev.xkmc.l2menustacker.screen.packets;

import java.util.function.Supplier;

public enum ScreenType {
   NONE(() -> ScreenTypeClient::none),
   PLAYER(() -> ScreenTypeClient::player);

   private final Supplier<Runnable> callback;

   ScreenType(Supplier<Runnable> callback) {
      this.callback = callback;
   }

   public void perform() {
      this.callback.get().run();
   }
}

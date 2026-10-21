package dev.xkmc.l2tabs.init.data;

import dev.xkmc.l2tabs.compat.api.TabCuriosCompat;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;

public enum OpenCurioHandler {
   CURIO_OPEN(TabCuriosCompat::openCuriosTab);

   private final Consumer<ServerPlayer> task;

   OpenCurioHandler(Consumer<ServerPlayer> run) {
      this.task = run;
   }

   public void invoke(ServerPlayer player) {
      this.task.accept(player);
   }
}

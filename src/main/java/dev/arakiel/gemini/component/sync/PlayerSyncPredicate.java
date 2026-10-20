package dev.arakiel.gemini.component.sync;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Contract;

public interface PlayerSyncPredicate {
   @Contract(
      pure = true
   )
   boolean shouldSyncWith(ServerPlayer var1);

   default boolean isRequiredOnClient() {
      return true;
   }

   static PlayerSyncPredicate all() {
      return p -> true;
   }

   static PlayerSyncPredicate only(Player player) {
      return p -> p == player;
   }
}

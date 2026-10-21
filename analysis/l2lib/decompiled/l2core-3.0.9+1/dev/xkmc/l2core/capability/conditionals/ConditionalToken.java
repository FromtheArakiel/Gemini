package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.world.entity.player.Player;

@SerialClass
public class ConditionalToken {
   public boolean tick(Player player) {
      return true;
   }

   public boolean retainOnDeath(Player player) {
      return false;
   }
}

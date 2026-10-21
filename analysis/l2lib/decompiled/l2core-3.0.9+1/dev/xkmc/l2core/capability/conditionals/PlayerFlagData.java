package dev.xkmc.l2core.capability.conditionals;

import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.TreeSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@SerialClass
public class PlayerFlagData extends PlayerCapabilityTemplate<PlayerFlagData> {
   @SerialField
   private final TreeSet<String> flags = new TreeSet<>();

   public static void addFlag(LivingEntity entity, String str) {
      if (entity instanceof Player player) {
         L2LibReg.FLAGS.type().getOrCreate(player).addFlag(str);
         if (player instanceof ServerPlayer sp) {
            L2LibReg.FLAGS.type().network.toClient(sp);
         }
      } else {
         entity.addTag(str);
      }
   }

   public static boolean hasFlag(LivingEntity entity, String str) {
      return entity instanceof Player player
         ? L2LibReg.FLAGS.type().getExisting(player).map(e -> e.hasFlag(str)).orElse(false)
         : entity.getTags().contains(str);
   }

   public void addFlag(String str) {
      this.flags.add(str);
   }

   public boolean hasFlag(String flag) {
      return this.flags.contains(flag);
   }

   public static void register() {
   }
}

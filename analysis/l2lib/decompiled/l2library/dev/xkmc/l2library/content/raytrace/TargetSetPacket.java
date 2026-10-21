package dev.xkmc.l2library.content.raytrace;

import dev.xkmc.l2serial.network.SerialPacketBase;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record TargetSetPacket(UUID player, @Nullable UUID target) implements SerialPacketBase<TargetSetPacket> {
   public void handle(@Nullable Player player) {
      RayTraceUtil.sync(this);
   }
}

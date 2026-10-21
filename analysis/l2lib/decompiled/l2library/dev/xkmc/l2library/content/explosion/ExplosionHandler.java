package dev.xkmc.l2library.content.explosion;

import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class ExplosionHandler {
   public static void explode(BaseExplosion exp) {
      if (!exp.base.level().isClientSide()) {
         if (!EventHooks.onExplosionStart(exp.base.level(), exp)) {
            exp.explode();
            Level level = exp.base.level();
            exp.finalizeExplosion(level.isClientSide());
            double x = exp.base.x();
            double y = exp.base.y();
            double z = exp.base.z();
            float r = exp.base.r();
            boolean flag = exp.mc.type() == BlockInteraction.KEEP;
            if (flag) {
               exp.clearToBlow();
            }

            for (Player player : level.players()) {
               if (player instanceof ServerPlayer serverplayer && serverplayer.distanceToSqr(x, y, z) < 4096.0) {
                  serverplayer.connection
                     .send(
                        new ClientboundExplodePacket(
                           x,
                           y,
                           z,
                           r,
                           exp.getToBlow(),
                           (Vec3)exp.getHitPlayers().get(serverplayer),
                           exp.mc.type(),
                           exp.particle.small(),
                           exp.particle.large(),
                           exp.particle.sound()
                        )
                     );
               }
            }
         }
      }
   }
}

package dev.xkmc.l2library.content.raytrace;

import com.google.common.collect.Maps;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2library.init.L2Library;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class RayTraceUtil {
   public static final int CLIENT_TIMEOUT = 100;
   public static final int SERVER_TIMEOUT = 200;
   public static final EntityTarget TARGET = new RayTraceUtil.EnderEntityTarget();
   public static final ConcurrentMap<UUID, RayTraceUtil.ServerTarget> TARGET_MAP = Maps.newConcurrentMap();

   @Nullable
   public static EntityHitResult rayTraceEntity(Player player, double reach, Predicate<Entity> pred) {
      Level world = player.level();
      Vec3 pos = new Vec3(player.getX(), player.getEyeY(), player.getZ());
      Vec3 end = getRayTerm(pos, player.getXRot(), player.getYRot(), reach);
      AABB box = new AABB(pos, end).inflate(1.0);
      return ProjectileUtil.getEntityHitResult(world, player, pos, end, box, pred);
   }

   public static BlockHitResult rayTraceBlock(Level worldIn, Player player, double reach) {
      float xRot = player.getXRot();
      float yRot = player.getYRot();
      Vec3 Vector3d = new Vec3(player.getX(), player.getEyeY(), player.getZ());
      Vec3 Vector3d1 = getRayTerm(Vector3d, xRot, yRot, reach);
      return worldIn.clip(new ClipContext(Vector3d, Vector3d1, Block.OUTLINE, Fluid.NONE, player));
   }

   public static Vec3 getRayTerm(Vec3 pos, float xRot, float yRot, double reach) {
      float f2 = Mth.cos(-yRot * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = Mth.sin(-yRot * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -Mth.cos(-xRot * (float) (Math.PI / 180.0));
      float f5 = Mth.sin(-xRot * (float) (Math.PI / 180.0));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      return pos.add(f6 * reach, f5 * reach, f7 * reach);
   }

   public static void serverTick(MinecraftServer server) {
      TARGET_MAP.entrySet().removeIf(e -> {
         ServerPlayer player = server.getPlayerList().getPlayer(e.getKey());
         if (player == null) {
            return true;
         } else {
            RayTraceUtil.ServerTarget target = e.getValue();
            Entity entity = player.serverLevel().getEntity(target.target);
            if (entity != null && !entity.isRemoved() && entity.isAlive()) {
               target.time++;
               return target.time >= 200;
            } else {
               return true;
            }
         }
      });
   }

   public static void sync(TargetSetPacket packet) {
      if (packet.target() == null) {
         TARGET_MAP.remove(packet.player());
      } else if (TARGET_MAP.containsKey(packet.player())) {
         RayTraceUtil.ServerTarget target = TARGET_MAP.get(packet.player());
         target.target = packet.target();
         target.time = 0;
      } else {
         TARGET_MAP.put(packet.player(), new RayTraceUtil.ServerTarget(packet.target()));
      }
   }

   public static void clientUpdateTarget(Player player, double range) {
      if (player.level().isClientSide()) {
         Vec3 vec3 = player.getEyePosition();
         Vec3 vec31 = player.getViewVector(1.0F).scale(range);
         Vec3 vec32 = vec3.add(vec31);
         AABB aabb = player.getBoundingBox().expandTowards(vec31).inflate(1.0);
         double sq = range * range;
         Predicate<Entity> predicate = e -> e instanceof LivingEntity && !e.isSpectator();
         EntityHitResult result = ProjectileUtil.getEntityHitResult(player, vec3, vec32, aabb, predicate, sq);
         if (result != null && vec3.distanceToSqr(result.getLocation()) < sq) {
            TARGET.updateTarget(result.getEntity());
         }
      }
   }

   @Nullable
   public static LivingEntity serverGetTarget(Player player) {
      if (player.level().isClientSide()) {
         return (LivingEntity)TARGET.target;
      }

      UUID id = player.getUUID();
      if (!TARGET_MAP.containsKey(id)) {
         return null;
      }

      UUID tid = TARGET_MAP.get(id).target;
      return tid == null ? null : (LivingEntity)((ServerLevel)player.level()).getEntity(tid);
   }

   public static class EnderEntityTarget extends EntityTarget {
      private int timeout = 0;

      public EnderEntityTarget() {
         super(3.0, 0.08726646259971647, 10);
      }

      @Override
      public void onChange(@Nullable Entity entity) {
         UUID eid = entity == null ? null : entity.getUUID();
         LocalPlayer pl = Proxy.getClientPlayer();
         if (pl != null) {
            L2Library.PACKET_HANDLER.toServer(new TargetSetPacket(pl.getUUID(), eid));
         }

         this.timeout = 0;
      }

      @Override
      public void tickRender() {
         super.tickRender();
         if (this.target != null) {
            this.timeout++;
            if (this.timeout > 100) {
               this.onChange(this.target);
            }
         }
      }
   }

   public static class ServerTarget {
      public UUID target;
      public int time;

      public ServerTarget(UUID target) {
         this.target = target;
         this.time = 0;
      }
   }
}

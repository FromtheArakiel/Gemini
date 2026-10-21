package dev.xkmc.l2core.base.effects.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public record DelayedEntityRender(
   LivingEntity entity, IconRenderRegion region, ResourceLocation rl, double xo, double yo, double zo, float tx, float ty, float tw, float th
) implements IDelayedRender {
   public DelayedEntityRender(LivingEntity entity, IconRenderRegion region, ResourceLocation rl, float tx, float ty, float tw, float th) {
      this(entity, region, rl, entity.xOld, entity.yOld, entity.zOld, tx, ty, tw, th);
   }

   public static DelayedEntityRender icon(LivingEntity entity, ResourceLocation rl) {
      return icon(entity, IconRenderRegion.identity(), rl);
   }

   public static DelayedEntityRender icon(LivingEntity entity, IconRenderRegion r, ResourceLocation rl) {
      return new DelayedEntityRender(entity, r, rl, 0.0F, 0.0F, 1.0F, 1.0F);
   }

   public DelayedEntityRender resize(IconRenderRegion r) {
      return new DelayedEntityRender(this.entity, r.resize(this.region), this.rl, this.xo, this.yo, this.zo, this.tx, this.ty, this.tw, this.th);
   }

   @Override
   public Vec3 pos(float partial) {
      LivingEntity entity = this.entity();
      float f = entity.getBbHeight() / 2.0F;
      double x0 = Mth.lerp(partial, this.xo(), entity.getX());
      double y0 = Mth.lerp(partial, this.yo(), entity.getY());
      double z0 = Mth.lerp(partial, this.zo(), entity.getZ());
      EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      Vec3 offset = dispatcher.getRenderer(entity).getRenderOffset(entity, partial);
      double x = x0 + offset.x();
      double y = y0 + offset.y() + f;
      double z = z0 + offset.z();
      return new Vec3(x, y, z);
   }
}

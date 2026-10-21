package dev.xkmc.l2core.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2core.base.effects.ClientEffectCap;
import dev.xkmc.l2core.base.effects.EffectToClient;
import dev.xkmc.l2core.base.effects.api.ClientRenderEffect;
import dev.xkmc.l2core.base.effects.api.DelayedEntityRender;
import dev.xkmc.l2core.base.effects.api.FirstPlayerRenderEffect;
import dev.xkmc.l2core.base.effects.api.IDelayedRender;
import dev.xkmc.l2core.base.effects.api.IconRenderRegion;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.init.L2CoreConfig;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Post;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2core", bus = Bus.GAME)
public class ClientEffectRenderEvents {
   private static final ArrayList<IDelayedRender> ICONS = new ArrayList<>();
   private static final RenderType DUMMY = RenderType.create(
      "entity_icon_preparation", DefaultVertexFormat.POSITION_TEX, Mode.QUADS, 256, false, false, CompositeState.builder().createCompositeState(false)
   );
   private static final Function<ResourceLocation, RenderType> ICON_TYPE = Util.memoize(
      rl -> RenderType.create(
         "entity_body_icon",
         DefaultVertexFormat.POSITION_TEX,
         Mode.QUADS,
         256,
         false,
         false,
         CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_TEX_SHADER)
            .setTextureState(new TextureStateShard(rl, false, false))
            .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .createCompositeState(false)
      )
   );

   public static void addIcon(IDelayedRender icon) {
      ICONS.add(icon);
   }

   @SubscribeEvent
   public static void clientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
      AbstractClientPlayer player = Proxy.getClientPlayer();
      if (player != null) {
         for (Entry<Holder<MobEffect>, MobEffectInstance> entry : player.getActiveEffectsMap().entrySet()) {
            if (entry.getValue().getEffect().value() instanceof FirstPlayerRenderEffect effect) {
               effect.onClientLevelRender(player, entry.getValue());
            }
         }
      }
   }

   public static RenderType get2DIcon(ResourceLocation id) {
      return ICON_TYPE.apply(id);
   }

   @SubscribeEvent
   public static void levelRenderLast(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_WEATHER) {
         if (!ICONS.isEmpty()) {
            if (!(Boolean)L2CoreConfig.CLIENT.renderOverlayIcons.get()) {
               ICONS.clear();
            } else {
               DUMMY.setupRenderState();
               DUMMY.clearRenderState();
               BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
               Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
               PoseStack stack = event.getPoseStack();
               float pTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
               Map<ResourceLocation, List<IDelayedRender>> map = new HashMap<>();

               for (IDelayedRender e : ICONS) {
                  map.computeIfAbsent(e.rl(), k -> new ArrayList<>()).add(e);
               }

               for (Entry<ResourceLocation, List<IDelayedRender>> ent : map.entrySet()) {
                  VertexConsumer vc = buffers.getBuffer(ICON_TYPE.apply(ent.getKey()));

                  for (IDelayedRender e : ent.getValue()) {
                     renderIcon(stack, vc, e, pTick, camera);
                  }
               }

               ICONS.clear();
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingEntityRender(Post<?, ?> event) {
      LivingEntity entity = event.getEntity();
      if (((GeneralCapabilityHolder)L2LibReg.EFFECT.type()).isProper(entity)) {
         if (!entity.getTags().contains("ClientOnly")) {
            ClientEffectCap cap = (ClientEffectCap)((GeneralCapabilityHolder)L2LibReg.EFFECT.type()).getOrCreate(entity);
            List<Pair<ClientRenderEffect, Integer>> l0 = new ArrayList<>();

            for (Entry<Holder<MobEffect>, Integer> entry : cap.map.entrySet()) {
               if (entry.getKey().value() instanceof ClientRenderEffect effect) {
                  l0.add(Pair.of(effect, entry.getValue()));
               }
            }

            if (!l0.isEmpty()) {
               List<Pair<Integer, DelayedEntityRender>> l1 = new ArrayList<>();
               int index = 0;

               for (Pair<ClientRenderEffect, Integer> e : l0) {
                  int lv = (Integer)e.getSecond();
                  int size = l1.size();
                  int I = index;
                  ((ClientRenderEffect)e.getFirst()).render(entity, lv, x -> l1.add(Pair.of(I, x)));
                  if (l1.size() > size) {
                     index++;
                  }
               }

               if (entity != Minecraft.getInstance().getCameraEntity()) {
                  int n = index;
                  int w = (int)Math.ceil(Math.sqrt(n));
                  int h = (int)Math.ceil(n * 1.0 / w);

                  for (Pair<Integer, DelayedEntityRender> e : l1) {
                     int i = (Integer)e.getFirst();
                     int iy = i / w;
                     int iw = Math.min(w, n - iy * w);
                     int ix = i - iy * w;
                     ICONS.add(((DelayedEntityRender)e.getSecond()).resize(IconRenderRegion.of(w, ix, iy, iw, h)));
                  }
               }
            }
         }
      }
   }

   private static void renderIcon(PoseStack pose, VertexConsumer vc, IDelayedRender icon, float partial, Camera camera) {
      Vec3 pos = icon.pos(partial);
      Vec3 cam_pos = camera.getPosition();
      pose.pushPose();
      pose.translate(pos.x() - cam_pos.x, pos.y() - cam_pos.y, pos.z() - cam_pos.z);
      pose.mulPose(camera.rotation());
      Pose entry = pose.last();
      float ix0 = -0.5F + icon.region().x();
      float ix1 = ix0 + icon.region().scale();
      float iy0 = -0.5F + icon.region().y();
      float iy1 = iy0 + icon.region().scale();
      float u0 = icon.tx();
      float v0 = icon.ty();
      float u1 = icon.tx() + icon.tw();
      float v1 = icon.ty() + icon.th();
      iconVertex(entry, vc, ix0, iy0, u0, v1);
      iconVertex(entry, vc, ix1, iy0, u1, v1);
      iconVertex(entry, vc, ix1, iy1, u1, v0);
      iconVertex(entry, vc, ix0, iy1, u0, v0);
      pose.popPose();
   }

   private static void iconVertex(Pose entry, VertexConsumer builder, float x, float y, float u, float v) {
      builder.addVertex(entry.pose(), x, y, 0.0F).setUv(u, v);
   }

   public static void sync(EffectToClient eff) {
      if (Minecraft.getInstance().level != null) {
         if (Minecraft.getInstance().level.getEntity(eff.entity()) instanceof LivingEntity le) {
            if (((GeneralCapabilityHolder)L2LibReg.EFFECT.type()).isProper(le)) {
               ClientEffectCap cap = (ClientEffectCap)((GeneralCapabilityHolder)L2LibReg.EFFECT.type()).getOrCreate(le);
               if (eff.exist()) {
                  cap.map.put(eff.effect(), eff.level());
               } else {
                  cap.map.remove(eff.effect());
               }
            }
         }
      }
   }
}

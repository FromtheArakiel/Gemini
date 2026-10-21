package dev.arakiel.gemini.component.internal.entity;

import com.mojang.datafixers.util.Unit;
import dev.arakiel.gemini.component.api.Component;
import dev.arakiel.gemini.component.api.ComponentKey;
import dev.arakiel.gemini.component.api.ComponentProvider;
import dev.arakiel.gemini.component.api.ComponentSync;
import dev.arakiel.gemini.component.api.ComponentCallbacks;
import dev.arakiel.gemini.component.api.RespawnCopyStrategy;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.base.ComponentsInternals;
import dev.arakiel.gemini.component.internal.network.MorePacketCodecs;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.Load;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.Unload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;

public final class GeminiEntityComponents {
   public static final Type<ComponentUpdatePayload<Integer>> PACKET_ID = ComponentUpdatePayload.id("entity_sync");
   public static final Type<ComponentUpdatePayload<Unit>> C2S_SELF_PACKET_ID = ComponentUpdatePayload.id("player_message_c2s");
   private static final Set<ResourceLocation> unknownC2SPlayerComponents = new HashSet<>();

   public static void init() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentUpdatePayload.register(PACKET_ID, ByteBufCodecs.VAR_INT);
         PayloadTypeRegistry.playC2S().register(C2S_SELF_PACKET_ID, ComponentUpdatePayload.codec(C2S_SELF_PACKET_ID, MorePacketCodecs.EMPTY));
         ComponentCallbacks.PlayerSync.EVENT.register((ComponentCallbacks.PlayerSync)player -> syncEntityComponents(player, player));
         ComponentCallbacks.TrackingStart.EVENT.register(GeminiEntityComponents::syncEntityComponents);
         ServerPlayNetworking.registerGlobalReceiver(
            C2S_SELF_PACKET_ID,
            (payload, ctx) -> {
               try {
                  Optional<ComponentKey<?>> componentKey = payload.componentKey();
                  if (componentKey.isPresent()) {
                     if (componentKey.get().getNullable(ctx.player()) instanceof ComponentSync.SelfMessaging synced) {
                        synced.handleC2SMessage(payload.buf());
                     } else if (payload.required()
                        && (unknownC2SPlayerComponents.add(payload.componentKeyId()) || FabricLoader.getInstance().isDevelopmentEnvironment())) {
                        ComponentsInternals.LOGGER
                           .warn(
                              "[Gemini Components] Received an update for component {} from player {}, but this component is not registered for player entities",
                              payload.componentKeyId(),
                              ctx.player()
                           );
                     }
                  } else if (payload.required()
                     && (unknownC2SPlayerComponents.add(payload.componentKeyId()) || FabricLoader.getInstance().isDevelopmentEnvironment())) {
                     ComponentsInternals.LOGGER
                        .warn("[Gemini Components] Received an update for unknown component {} from player {}", payload.componentKeyId(), ctx.player());
                  }
               } finally {
                  payload.buf().release();
               }
            }
         );
      }

      if (FabricLoader.getInstance().isModLoaded("fabric-lifecycle-events-v1")) {
         ServerEntityEvents.ENTITY_LOAD.register((Load)(entity, world) -> ((ComponentProvider)entity).getComponentContainer().onServerLoad());
         ServerEntityEvents.ENTITY_UNLOAD.register((Unload)(entity, world) -> ((ComponentProvider)entity).getComponentContainer().onServerUnload());
      }

      ServerLivingEntityEvents.MOB_CONVERSION.register(RespawnCopyStrategy.EVENT_PHASE, GeminiEntityComponents::copyData);
      ServerPlayerEvents.COPY_FROM.register(RespawnCopyStrategy.EVENT_PHASE, GeminiEntityComponents::copyData);
      StaticEntityComponentPlugin.INSTANCE.ensureInitialized();
   }

   private static void copyData(LivingEntity original, LivingEntity clone, boolean keepInventory) {
      for (ComponentKey<?> key : ((ComponentProvider)original).getComponentContainer().keys()) {
         if (key.isProvidedBy(clone)) {
            copyData(original, clone, original.registryAccess(), false, keepInventory, true, key);
         }
      }
   }

   private static void copyData(ServerPlayer original, ServerPlayer clone, boolean lossless) {
      boolean keepInventory = original.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || clone.isSpectator();

      for (ComponentKey<?> key : ((ComponentProvider)original).getComponentContainer().keys()) {
         copyData(original, clone, original.registryAccess(), lossless, keepInventory, !((SwitchablePlayerEntity)original).gemini$isSwitchingCharacter(), key);
      }
   }

   private static <C extends Component> void copyData(
      LivingEntity original, LivingEntity clone, Provider registryLookup, boolean lossless, boolean keepInventory, boolean sameCharacter, ComponentKey<C> key
   ) {
      C from = key.get(original);
      C to = key.get(clone);
      RespawnCopyStrategy.get(key, (Class<? extends LivingEntity>)original.getClass())
         .copyForRespawn(from, to, registryLookup, lossless, keepInventory, sameCharacter);
   }

   private static void syncEntityComponents(ServerPlayer player, Entity tracked) {
      ComponentProvider provider = (ComponentProvider)tracked;

      for (ComponentKey<?> key : provider.getComponentContainer().keys()) {
         key.syncWith(player, provider);
      }
   }
}
package dev.arakiel.gemini.component.internal.world;

import com.mojang.datafixers.util.Unit;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.world.WorldSyncCallback;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.network.MorePacketCodecs;
import java.util.IdentityHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.Load;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.Unload;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class GeminiWorldComponents {
   public static final Type<ComponentUpdatePayload<Unit>> PACKET_ID = ComponentUpdatePayload.id("world_sync");
   private static final Map<ResourceKey<Level>, ComponentContainer.Factory<Level>> worldContainerFactories = new IdentityHashMap<>();

   public static void init() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentUpdatePayload.register(PACKET_ID, MorePacketCodecs.EMPTY);
         WorldSyncCallback.EVENT.register((WorldSyncCallback)(player, world) -> {
            ComponentProvider provider = (ComponentProvider)world;

            for (ComponentKey<?> key : provider.getComponentContainer().keys()) {
               key.syncWith(player, provider);
            }
         });
      }

      if (FabricLoader.getInstance().isModLoaded("fabric-lifecycle-events-v1")) {
         ServerWorldEvents.LOAD.register((Load)(server, world) -> ((ComponentProvider)world).getComponentContainer().onServerLoad());
         ServerWorldEvents.UNLOAD.register((Unload)(server, world) -> ((ComponentProvider)world).getComponentContainer().onServerUnload());
      }

      StaticWorldComponentPlugin.INSTANCE.ensureInitialized();
   }

   public static ComponentContainer createComponents(Level world) {
      return worldContainerFactories.computeIfAbsent(world.dimension(), GeminiWorldComponents::getWorldComponentFactory).createContainer(world);
   }

   private static synchronized ComponentContainer.Factory<Level> getWorldComponentFactory(ResourceKey<Level> dimensionKey) {
      ComponentContainer.Factory<Level> existing = worldContainerFactories.get(dimensionKey);
      if (existing != null) {
         return existing;
      } else {
         return StaticWorldComponentPlugin.INSTANCE.requiresStaticFactory(dimensionKey)
            ? StaticWorldComponentPlugin.INSTANCE.buildDedicatedFactory(dimensionKey)
            : worldContainerFactories.computeIfAbsent(null, n -> StaticWorldComponentPlugin.INSTANCE.buildDedicatedFactory(null));
      }
   }
}

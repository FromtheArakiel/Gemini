package dev.arakiel.gemini.component.internal.level;

import com.mojang.datafixers.util.Unit;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.world.WorldSyncCallback;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.network.MorePacketCodecs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.level.storage.LevelData;

public final class GeminiLevelComponents {
   public static final Type<ComponentUpdatePayload<Unit>> PACKET_ID = ComponentUpdatePayload.id("level_sync");

   public static void init() {
      if (FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1")) {
         ComponentUpdatePayload.register(PACKET_ID, MorePacketCodecs.EMPTY);
         if (FabricLoader.getInstance().isModLoaded("gemini:components-world")) {
            WorldSyncCallback.EVENT.register((WorldSyncCallback)(player, world) -> {
               LevelData props = world.getLevelData();

               for (ComponentKey<?> key : props.asComponentProvider().getComponentContainer().keys()) {
                  key.syncWith(player, props.asComponentProvider());
               }
            });
         }
      }

      StaticLevelComponentPlugin.INSTANCE.ensureInitialized();
   }
}

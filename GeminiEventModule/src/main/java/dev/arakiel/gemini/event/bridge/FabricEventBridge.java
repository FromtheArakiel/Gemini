package dev.arakiel.gemini.event.bridge;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.command.RegisterCommandsEvent;
import dev.arakiel.gemini.event.entity.living.LivingEquipmentChangeEvent;
import dev.arakiel.gemini.event.entity.player.PlayerCloneEvent;
import dev.arakiel.gemini.event.entity.player.PlayerInteractEntityEvent;
import dev.arakiel.gemini.event.entity.player.PlayerRespawnEvent;
import dev.arakiel.gemini.event.entity.player.PlayerTickEvent;
import dev.arakiel.gemini.event.level.EntityJoinLevelEvent;
import dev.arakiel.gemini.event.level.EntityLeaveLevelEvent;
import dev.arakiel.gemini.event.level.LevelTickEvent;
import dev.arakiel.gemini.event.resource.DataPackReloadEvent;
import dev.arakiel.gemini.event.resource.DatapackSyncEvent;
import dev.arakiel.gemini.event.resource.LootTableLoadEvent;
import dev.arakiel.gemini.event.resource.TagsUpdatedEvent;
import dev.arakiel.gemini.event.server.PlayerLoggedInEvent;
import dev.arakiel.gemini.event.server.PlayerLoggedOutEvent;
import dev.arakiel.gemini.event.server.ServerLifecycleEvent;
import dev.arakiel.gemini.event.server.ServerSaveEvent;
import dev.arakiel.gemini.event.server.ServerTickEvent;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;

public final class FabricEventBridge {
    private static volatile MinecraftServer server;

    private FabricEventBridge() {
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                GeminiEvents.post(new ServerLifecycleEvent(server, ServerLifecycleEvent.Stage.STARTING)));
        ServerLifecycleEvents.SERVER_STARTING.register(current -> FabricEventBridge.server = current);
        ServerLifecycleEvents.SERVER_STARTED.register(current -> {
            FabricEventBridge.server = current;
            GeminiEvents.post(new ServerLifecycleEvent(current, ServerLifecycleEvent.Stage.STARTED));
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(current ->
                GeminiEvents.post(new ServerLifecycleEvent(current, ServerLifecycleEvent.Stage.STOPPING)));
        ServerLifecycleEvents.SERVER_STOPPED.register(current -> {
            GeminiEvents.post(new ServerLifecycleEvent(current, ServerLifecycleEvent.Stage.STOPPED));
            FabricEventBridge.server = null;
        });

        ServerLifecycleEvents.BEFORE_SAVE.register((server, flush, force) ->
                GeminiEvents.post(new ServerSaveEvent(server, ServerSaveEvent.Phase.PRE, flush, force)));
        ServerLifecycleEvents.AFTER_SAVE.register((server, flush, force) ->
                GeminiEvents.post(new ServerSaveEvent(server, ServerSaveEvent.Phase.POST, flush, force)));

        ServerTickEvents.START_SERVER_TICK.register(server ->
        {
            GeminiEvents.post(new ServerTickEvent(server, ServerTickEvent.Phase.PRE));
            for (var player : server.getPlayerList().getPlayers()) {
                GeminiEvents.post(new PlayerTickEvent(player, PlayerTickEvent.Phase.PRE));
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(server ->
        {
            for (var player : server.getPlayerList().getPlayers()) {
                GeminiEvents.post(new PlayerTickEvent(player, PlayerTickEvent.Phase.POST));
            }
            GeminiEvents.post(new ServerTickEvent(server, ServerTickEvent.Phase.POST));
        });
        ServerTickEvents.START_WORLD_TICK.register(level ->
                GeminiEvents.post(new LevelTickEvent(level, LevelTickEvent.Phase.PRE)));
        ServerTickEvents.END_WORLD_TICK.register(level ->
                GeminiEvents.post(new LevelTickEvent(level, LevelTickEvent.Phase.POST)));

        ServerEntityEvents.ENTITY_LOAD.register((entity, level) ->
                GeminiEvents.post(new EntityJoinLevelEvent(entity, level)));
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) ->
                GeminiEvents.post(new EntityLeaveLevelEvent(entity, level)));
        ServerEntityEvents.EQUIPMENT_CHANGE.register((entity, slot, previous, current) ->
                GeminiEvents.post(new LivingEquipmentChangeEvent(entity, slot, previous, current)));
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
                GeminiEvents.post(new PlayerCloneEvent(oldPlayer, newPlayer, !alive)));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                GeminiEvents.post(new PlayerRespawnEvent(oldPlayer, newPlayer, !alive)));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                GeminiEvents.post(new PlayerLoggedInEvent(handler.player)));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                GeminiEvents.post(new PlayerLoggedOutEvent(handler.player)));

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, entity, hand);
            GeminiEvents.post(event);
            return event.isCanceled() ? InteractionResult.FAIL : InteractionResult.PASS;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) ->
                GeminiEvents.post(new RegisterCommandsEvent(
                        requireServer(),
                        dispatcher,
                        requireServer().registryAccess(),
                        environment
                )));
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
                GeminiEvents.post(new DatapackSyncEvent(player, joined)));
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) ->
                GeminiEvents.post(new DataPackReloadEvent(server, DataPackReloadEvent.Phase.START, true)));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) ->
                GeminiEvents.post(new DataPackReloadEvent(server, DataPackReloadEvent.Phase.END, success)));
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) ->
                GeminiEvents.post(new TagsUpdatedEvent(registries, client)));
        LootTableEvents.REPLACE.register((key, original, source, registries) -> {
            LootTableLoadEvent event = new LootTableLoadEvent(key, original, source.isBuiltin());
            GeminiEvents.post(event);
            return event.getTable() == original ? null : event.getTable();
        });
    }

    private static MinecraftServer requireServer() {
        MinecraftServer current = FabricEventBridge.server;
        if (current == null) {
            throw new IllegalStateException("Gemini command registration requires an active server");
        }
        return current;
    }
}

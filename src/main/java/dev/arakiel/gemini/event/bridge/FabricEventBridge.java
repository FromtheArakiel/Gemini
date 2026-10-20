package dev.arakiel.gemini.event.bridge;

import dev.arakiel.gemini.api.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingEquipmentChangeEvent;
import dev.arakiel.gemini.event.entity.player.PlayerInteractEntityEvent;
import dev.arakiel.gemini.event.level.EntityJoinLevelEvent;
import dev.arakiel.gemini.event.level.EntityLeaveLevelEvent;
import dev.arakiel.gemini.event.level.LevelTickEvent;
import dev.arakiel.gemini.event.server.PlayerLoggedInEvent;
import dev.arakiel.gemini.event.server.PlayerLoggedOutEvent;
import dev.arakiel.gemini.event.server.ServerLifecycleEvent;
import dev.arakiel.gemini.event.server.ServerSaveEvent;
import dev.arakiel.gemini.event.server.ServerTickEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.InteractionResult;

public final class FabricEventBridge {
    private FabricEventBridge() {
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                GeminiEvents.post(new ServerLifecycleEvent(server, ServerLifecycleEvent.Stage.STARTING)));
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                GeminiEvents.post(new ServerLifecycleEvent(server, ServerLifecycleEvent.Stage.STARTED)));
        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
                GeminiEvents.post(new ServerLifecycleEvent(server, ServerLifecycleEvent.Stage.STOPPING)));
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
                GeminiEvents.post(new ServerLifecycleEvent(server, ServerLifecycleEvent.Stage.STOPPED)));

        ServerLifecycleEvents.BEFORE_SAVE.register((server, flush, force) ->
                GeminiEvents.post(new ServerSaveEvent(server, ServerSaveEvent.Phase.PRE, flush, force)));
        ServerLifecycleEvents.AFTER_SAVE.register((server, flush, force) ->
                GeminiEvents.post(new ServerSaveEvent(server, ServerSaveEvent.Phase.POST, flush, force)));

        ServerTickEvents.START_SERVER_TICK.register(server ->
                GeminiEvents.post(new ServerTickEvent(server, ServerTickEvent.Phase.PRE)));
        ServerTickEvents.END_SERVER_TICK.register(server ->
                GeminiEvents.post(new ServerTickEvent(server, ServerTickEvent.Phase.POST)));
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

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                GeminiEvents.post(new PlayerLoggedInEvent(handler.player)));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                GeminiEvents.post(new PlayerLoggedOutEvent(handler.player)));

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, entity, hand);
            GeminiEvents.post(event);
            return event.isCanceled() ? InteractionResult.FAIL : InteractionResult.PASS;
        });
    }
}

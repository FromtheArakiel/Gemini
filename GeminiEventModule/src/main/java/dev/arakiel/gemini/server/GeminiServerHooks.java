package dev.arakiel.gemini.server;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.resource.AddReloadListenerEvent;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Objects;

public final class GeminiServerHooks {
    private GeminiServerHooks() {
    }

    public static void registerReloadListeners() {
        AddReloadListenerEvent event = new AddReloadListenerEvent();
        GeminiEvents.post(event);
        for (var listener : event.getListeners()) {
            ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(listener);
        }
    }

    public static void addReloadListener(SimpleSynchronousResourceReloadListener listener) {
        Objects.requireNonNull(listener, "listener");
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(listener);
    }

    public static ResourceManager serverResourceManager(net.minecraft.server.MinecraftServer server) {
        return Objects.requireNonNull(server, "server").getResourceManager();
    }

    public static boolean addBuiltinResourcePack(
            ResourceLocation id,
            ModContainer container,
            Component displayName,
            ResourcePackActivationType activationType
    ) {
        return ResourceManagerHelper.registerBuiltinResourcePack(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(container, "container"),
                Objects.requireNonNull(displayName, "displayName"),
                Objects.requireNonNull(activationType, "activationType")
        );
    }
}

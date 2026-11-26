package dev.arakiel.gemini.event.resource;

import com.mojang.logging.LogUtils;
import dev.arakiel.gemini.event.GeminiEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class GeminiServerReloadBridge implements IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath("gemini", "server_reload_bridge");
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager resourceManager,
            ProfilerFiller prepareProfiler,
            ProfilerFiller applyProfiler,
            Executor prepareExecutor,
            Executor applyExecutor
    ) {
        AddReloadListenerEvent event = new AddReloadListenerEvent();
        GeminiEvents.post(event);
        CompletableFuture<?>[] futures = event.getListeners().stream()
                .map(listener -> listener.reload(
                        barrier,
                        resourceManager,
                        prepareProfiler,
                        applyProfiler,
                        prepareExecutor,
                        applyExecutor
                ))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures).exceptionally(throwable -> {
            LOGGER.error("Gemini reload listener failed", throwable);
            return null;
        });
    }
}

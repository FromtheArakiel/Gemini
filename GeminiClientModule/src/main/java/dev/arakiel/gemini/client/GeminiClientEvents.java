package dev.arakiel.gemini.client;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.ClientPlayerNetworkEvent;
import dev.arakiel.gemini.event.client.ClientTickEvent;
import dev.arakiel.gemini.event.client.ContainerScreenEvent;
import dev.arakiel.gemini.event.client.EntityRenderersEvent;
import dev.arakiel.gemini.event.client.RegisterKeyMappingsEvent;
import dev.arakiel.gemini.event.client.RegisterMenuScreensEvent;
import dev.arakiel.gemini.event.client.RegisterParticleProvidersEvent;
import dev.arakiel.gemini.event.client.ScreenEvent;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import dev.arakiel.gemini.event.entity.player.PlayerTickEvent;
import net.minecraft.client.gui.screens.Screen;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class GeminiClientEvents {
    private static final Set<Screen> INSTRUMENTED_SCREENS =
            Collections.newSetFromMap(new WeakHashMap<>());

    private GeminiClientEvents() {
    }

    public static void init() {
        Minecraft client = Minecraft.getInstance();
        ClientTickEvents.START_CLIENT_TICK.register(ignored -> {
            GeminiEvents.post(new ClientTickEvent(client, ClientTickEvent.Phase.PRE));
            if (client.player != null) {
                GeminiEvents.post(new PlayerTickEvent(client.player, PlayerTickEvent.Phase.PRE));
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(ignored -> {
            if (client.player != null) {
                GeminiEvents.post(new PlayerTickEvent(client.player, PlayerTickEvent.Phase.POST));
            }
            GeminiEvents.post(new ClientTickEvent(client, ClientTickEvent.Phase.POST));
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, ignored) ->
                GeminiEvents.post(new ClientPlayerNetworkEvent(
                        ClientPlayerNetworkEvent.Phase.LOGGING_IN,
                        client.player,
                        null
                )));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, ignored) ->
                GeminiEvents.post(new ClientPlayerNetworkEvent(
                        ClientPlayerNetworkEvent.Phase.LOGGING_OUT,
                        client.player,
                        null
                )));

        ScreenEvents.BEFORE_INIT.register((ignored, screen, width, height) ->
                GeminiEvents.post(new ScreenEvent(
                        client,
                        screen,
                        null,
                        0,
                        0,
                        0.0F,
                        ScreenEvent.Phase.INIT_PRE
                )));
        ScreenEvents.AFTER_INIT.register((ignored, screen, width, height) -> {
            GeminiEvents.post(new ScreenEvent(
                    client,
                    screen,
                    null,
                    0,
                    0,
                    0.0F,
                    ScreenEvent.Phase.INIT_POST
            ));
            if (!INSTRUMENTED_SCREENS.add(screen)) {
                return;
            }
            ScreenEvents.beforeRender(screen).register((current, graphics, mouseX, mouseY, tickDelta) ->
                    GeminiEvents.post(new ScreenEvent(
                            client,
                            current,
                            graphics,
                            mouseX,
                            mouseY,
                            tickDelta,
                            ScreenEvent.Phase.RENDER_PRE
                    )));
            ScreenEvents.afterRender(screen).register((current, graphics, mouseX, mouseY, tickDelta) ->
                    GeminiEvents.post(new ScreenEvent(
                            client,
                            current,
                            graphics,
                            mouseX,
                            mouseY,
                            tickDelta,
                            ScreenEvent.Phase.RENDER_POST
                    )));
            ScreenEvents.remove(screen).register(current ->
                    GeminiEvents.post(new ScreenEvent(
                            client,
                            current,
                            null,
                            0,
                            0,
                            0.0F,
                            ScreenEvent.Phase.CLOSING
                    )));
            ScreenEvents.remove(screen).register(INSTRUMENTED_SCREENS::remove);
            if (screen instanceof AbstractContainerScreen<?> containerScreen) {
                ScreenEvents.beforeRender(screen).register((current, graphics, mouseX, mouseY, tickDelta) ->
                        GeminiEvents.post(new ContainerScreenEvent(
                                containerScreen,
                                graphics,
                                mouseX,
                                mouseY,
                                tickDelta,
                                ContainerScreenEvent.Phase.BACKGROUND
                        )));
                ScreenEvents.afterRender(screen).register((current, graphics, mouseX, mouseY, tickDelta) ->
                        GeminiEvents.post(new ContainerScreenEvent(
                                containerScreen,
                                graphics,
                                mouseX,
                                mouseY,
                                tickDelta,
                                ContainerScreenEvent.Phase.FOREGROUND
                        )));
            }
        });

        RegisterMenuScreensEvent menuScreens = new RegisterMenuScreensEvent();
        GeminiEvents.post(menuScreens);
        for (var registration : menuScreens.getRegistrations()) {
            registerMenuScreen(registration);
        }

        RegisterKeyMappingsEvent keyMappings = new RegisterKeyMappingsEvent();
        GeminiEvents.post(keyMappings);
        keyMappings.getMappings().forEach(KeyBindingHelper::registerKeyBinding);

        RegisterParticleProvidersEvent particleProviders = new RegisterParticleProvidersEvent();
        GeminiEvents.post(particleProviders);
        for (var registration : particleProviders.getRegistrations()) {
            registerParticle(registration);
        }

        EntityRenderersEvent entityRenderers = new EntityRenderersEvent();
        GeminiEvents.post(entityRenderers);
        for (var registration : entityRenderers.getRegistrations()) {
            registerEntityRenderer(registration);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerMenuScreen(RegisterMenuScreensEvent.Registration<?, ?> registration) {
        MenuScreens.register((net.minecraft.world.inventory.MenuType) registration.type(), registration.constructor());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerParticle(RegisterParticleProvidersEvent.Registration<?> registration) {
        registerParticleTyped(
                (net.minecraft.core.particles.ParticleType) registration.type(),
                (net.minecraft.client.particle.ParticleProvider) registration.provider()
        );
    }

    private static <T extends net.minecraft.core.particles.ParticleOptions> void registerParticleTyped(
            net.minecraft.core.particles.ParticleType<T> type,
            net.minecraft.client.particle.ParticleProvider<T> provider
    ) {
        ParticleFactoryRegistry.getInstance().register(type, provider);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerEntityRenderer(EntityRenderersEvent.Registration<?> registration) {
        EntityRendererRegistry.register((net.minecraft.world.entity.EntityType) registration.type(), registration.provider());
    }
}

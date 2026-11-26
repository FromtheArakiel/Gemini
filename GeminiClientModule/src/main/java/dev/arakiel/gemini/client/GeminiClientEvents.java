package dev.arakiel.gemini.client;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.ClientPlayerNetworkEvent;
import dev.arakiel.gemini.event.client.ClientChatEvent;
import dev.arakiel.gemini.event.client.ClientTickEvent;
import dev.arakiel.gemini.event.client.ContainerScreenEvent;
import dev.arakiel.gemini.event.client.EntityRenderersEvent;
import dev.arakiel.gemini.event.client.RenderHighlightEvent;
import dev.arakiel.gemini.event.client.RenderLevelStageEvent;
import dev.arakiel.gemini.event.client.RegisterKeyMappingsEvent;
import dev.arakiel.gemini.event.client.RegisterMenuScreensEvent;
import dev.arakiel.gemini.event.client.RegisterParticleProvidersEvent;
import dev.arakiel.gemini.event.client.RegisterClientCommandsEvent;
import dev.arakiel.gemini.event.client.RegisterClientReloadListenersEvent;
import dev.arakiel.gemini.event.client.RegisterColorHandlersEvent;
import dev.arakiel.gemini.event.client.RegisterEntityRenderersEvent;
import dev.arakiel.gemini.event.client.ModelEvent;
import dev.arakiel.gemini.event.client.RegisterShadersEvent;
import dev.arakiel.gemini.event.client.RecipesUpdatedEvent;
import dev.arakiel.gemini.event.client.ScreenEvent;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.geom.EntityModelSet;
import dev.arakiel.gemini.event.entity.player.PlayerTickEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.PackType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class GeminiClientEvents {
    private static final Set<Screen> INSTRUMENTED_SCREENS =
            Collections.newSetFromMap(new WeakHashMap<>());
    private static final ThreadLocal<ClientChatEvent> PENDING_SEND_CHAT = new ThreadLocal<>();
    private static boolean initialized;

    private GeminiClientEvents() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            initialized = false;
            throw new IllegalStateException("Gemini client events must initialize after Minecraft is constructed");
        }
        postClientRegistrations(minecraft);
        ModelLoadingPlugin.register(context -> {
            ModelEvent registerModels = new ModelEvent(
                    ModelEvent.Phase.REGISTER_ADDITIONAL,
                    new ArrayList<>(),
                    Map.of()
            );
            GeminiEvents.post(registerModels);
            context.addModels(registerModels.getAdditionalModels());
            context.modifyModelAfterBake().register((model, bakeContext) -> {
                ModelEvent modify = new ModelEvent(
                        ModelEvent.Phase.MODIFY_BAKING_RESULT,
                        new ArrayList<>(),
                        Map.of(bakeContext.resourceId(), model)
                );
                GeminiEvents.post(modify);
                return modify.getBakingResult().getOrDefault(bakeContext.resourceId(), model);
            });
        });
        CoreShaderRegistrationCallback.EVENT.register(context -> {
            RegisterShadersEvent event = new RegisterShadersEvent();
            GeminiEvents.post(event);
            for (RegisterShadersEvent.Registration registration : event.getRegistrations()) {
                if (registration.vertexFormat() != null
                        && registration.loader() instanceof RegisterShadersEvent.ShaderLoader loader) {
                    context.register(
                            registration.id(),
                            registration.vertexFormat(),
                            loader::load
                    );
                }
            }
        });
        ClientTickEvents.START_CLIENT_TICK.register(ignored -> {
            Minecraft client = Minecraft.getInstance();
            GeminiEvents.post(new ClientTickEvent(client, ClientTickEvent.Phase.PRE));
            if (client.player != null) {
                GeminiEvents.post(new PlayerTickEvent(client.player, PlayerTickEvent.Phase.PRE));
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(ignored -> {
            Minecraft client = Minecraft.getInstance();
            if (client.player != null) {
                GeminiEvents.post(new PlayerTickEvent(client.player, PlayerTickEvent.Phase.POST));
            }
            GeminiEvents.post(new ClientTickEvent(client, ClientTickEvent.Phase.POST));
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, ignored) -> {
            Minecraft client = Minecraft.getInstance();
            GeminiEvents.post(new ClientPlayerNetworkEvent(
                        ClientPlayerNetworkEvent.Phase.LOGGING_IN,
                        client.player,
                        null
                ));
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, ignored) -> {
            Minecraft client = Minecraft.getInstance();
            GeminiEvents.post(new ClientPlayerNetworkEvent(
                        ClientPlayerNetworkEvent.Phase.LOGGING_OUT,
                        client.player,
                        null
                ));
        });
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            ClientChatEvent event = new ClientChatEvent(message);
            PENDING_SEND_CHAT.set(event);
            GeminiEvents.post(event);
            if (event.isCanceled()) {
                PENDING_SEND_CHAT.remove();
                return false;
            }
            return true;
        });
        ClientSendMessageEvents.MODIFY_CHAT.register(message -> {
            ClientChatEvent event = PENDING_SEND_CHAT.get();
            PENDING_SEND_CHAT.remove();
            if (event == null) {
                event = new ClientChatEvent(message);
                GeminiEvents.post(event);
            }
            return event.getMessage();
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registries) ->
                GeminiEvents.post(new RegisterClientCommandsEvent(dispatcher)));
        WorldRenderEvents.BEFORE_ENTITIES.register(context ->
                postRenderLevelStage(context, RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS));
        WorldRenderEvents.AFTER_ENTITIES.register(context ->
                postRenderLevelStage(context, RenderLevelStageEvent.Stage.AFTER_ENTITIES));
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context ->
                postRenderLevelStage(context, RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS));
        WorldRenderEvents.LAST.register(context ->
                postRenderLevelStage(context, RenderLevelStageEvent.Stage.AFTER_LEVEL));
        WorldRenderEvents.BLOCK_OUTLINE.register((context, outline) -> {
            if (context.matrixStack() == null) {
                return true;
            }
            RenderHighlightEvent event = new RenderHighlightEvent(
                    context.matrixStack(),
                    context.camera(),
                    RenderHighlightEvent.Phase.BLOCK,
                    null,
                    null
            );
            GeminiEvents.post(event);
            return !event.isCanceled();
        });

        ScreenEvents.BEFORE_INIT.register((client, screen, width, height) ->
                GeminiEvents.post(new ScreenEvent(
                        client,
                        screen,
                        null,
                        0,
                        0,
                        0.0F,
                        ScreenEvent.Phase.INIT_PRE
                )));
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
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
                            Minecraft.getInstance(),
                            current,
                            graphics,
                            mouseX,
                            mouseY,
                            tickDelta,
                            ScreenEvent.Phase.RENDER_PRE
                    )));
            ScreenEvents.afterRender(screen).register((current, graphics, mouseX, mouseY, tickDelta) ->
                    GeminiEvents.post(new ScreenEvent(
                            Minecraft.getInstance(),
                            current,
                            graphics,
                            mouseX,
                            mouseY,
                            tickDelta,
                            ScreenEvent.Phase.RENDER_POST
                    )));
            ScreenEvents.remove(screen).register(current ->
                    GeminiEvents.post(new ScreenEvent(
                            Minecraft.getInstance(),
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

    }

    private static void postClientRegistrations(Minecraft client) {
        RegisterMenuScreensEvent menuScreens = new RegisterMenuScreensEvent();
        GeminiEvents.post(menuScreens);
        for (var registration : menuScreens.getRegistrations()) {
            registerMenuScreen(registration);
        }

        RegisterKeyMappingsEvent keyMappings = new RegisterKeyMappingsEvent();
        GeminiEvents.post(keyMappings);
        keyMappings.getMappings().forEach(KeyBindingHelper::registerKeyBinding);

        RegisterClientReloadListenersEvent reloadListeners = new RegisterClientReloadListenersEvent();
        GeminiEvents.post(reloadListeners);
        for (var listener : reloadListeners.getListeners()) {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(listener);
        }

        RegisterColorHandlersEvent colorHandlers = new RegisterColorHandlersEvent();
        GeminiEvents.post(colorHandlers);
        for (var registration : colorHandlers.getBlockRegistrations()) {
            ColorProviderRegistry.BLOCK.register(
                    registration.provider(),
                    registration.blocks().toArray(net.minecraft.world.level.block.Block[]::new)
            );
        }
        for (var registration : colorHandlers.getItemRegistrations()) {
            ColorProviderRegistry.ITEM.register(
                    registration.provider(),
                    registration.items().toArray(net.minecraft.world.item.Item[]::new)
            );
        }

        EntityModelSet entityModels = client.getEntityModels();
        RegisterEntityRenderersEvent modelLayers = new RegisterEntityRenderersEvent(
                entityModels == null ? new EntityModelSet() : entityModels
        );
        GeminiEvents.post(modelLayers);
        for (var registration : modelLayers.getLayers()) {
            EntityModelLayerRegistry.registerModelLayer(
                    registration.location(),
                    registration::definition
            );
        }

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

    private static void postRenderLevelStage(
            WorldRenderContext context,
            RenderLevelStageEvent.Stage stage
    ) {
        if (context.matrixStack() != null && context.frustum() != null) {
            GeminiEvents.post(new RenderLevelStageEvent(
                    context.worldRenderer(),
                    context.matrixStack(),
                    context.camera(),
                    context.frustum(),
                    context.tickCounter().getGameTimeDeltaPartialTick(false),
                    stage
            ));
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

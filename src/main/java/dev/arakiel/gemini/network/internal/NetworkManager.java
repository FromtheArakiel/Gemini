package dev.arakiel.gemini.network.internal;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.network.api.NetworkChannel;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class NetworkManager {
    private static final Map<ResourceLocation, NetworkChannel> CHANNELS = new ConcurrentHashMap<>();
    private static final Map<UUID, Set<ResourceLocation>> SUPPORTED_CHANNELS = new ConcurrentHashMap<>();
    private static volatile Consumer<CustomPacketPayload> clientSender = payload -> {
        throw new IllegalStateException("Client network sender is not initialized");
    };
    private static volatile Supplier<RegistryAccess> clientRegistryAccess = () -> RegistryAccess.EMPTY;
    private static volatile Predicate<CustomPacketPayload.Type<?>> clientTransportCheck = type -> true;
    private static volatile Set<ResourceLocation> clientServerChannels = Set.of();
    private static volatile Set<ResourceLocation> clientMissingRequiredChannels = Set.of();
    private static volatile boolean clientHandshakeComplete;
    private static volatile boolean clientHandshakeAccepted;
    private static volatile String clientHandshakeMessage = "";
    private static volatile boolean registrationsLocked;
    private static boolean initialized;

    private NetworkManager() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        PayloadTypeRegistry.playC2S().register(NetworkHandshake.HelloPayload.TYPE, NetworkHandshake.HelloPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(NetworkHandshake.ResponsePayload.TYPE, NetworkHandshake.ResponsePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(NetworkBundle.TYPE, NetworkBundle.CODEC);
        PayloadTypeRegistry.playS2C().register(NetworkBundle.TYPE, NetworkBundle.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
                NetworkHandshake.HelloPayload.TYPE,
                (payload, context) -> {
                    try {
                        handleHello(payload.data(), context.player());
                    } catch (RuntimeException exception) {
                        Gemini.LOGGER.error("Failed to process Gemini network handshake", exception);
                    }
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(
                NetworkBundle.TYPE,
                (payload, context) -> {
                    try {
                        dispatchBundle(payload, context.player());
                    } catch (RuntimeException exception) {
                        Gemini.LOGGER.error("Failed to process Gemini network bundle", exception);
                    }
                }
        );
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                SUPPORTED_CHANNELS.remove(handler.player.getUUID()));
    }

    public static synchronized NetworkChannel channel(ResourceLocation id) {
        Objects.requireNonNull(id, "id");
        if (registrationsLocked && !CHANNELS.containsKey(id)) {
            throw new IllegalStateException("Network registrations are locked; cannot create channel " + id);
        }
        return CHANNELS.computeIfAbsent(id, NetworkChannel::new);
    }

    public static NetworkChannel getChannel(ResourceLocation id) {
        return CHANNELS.get(id);
    }

    public static Collection<NetworkChannel> channels() {
        return List.copyOf(CHANNELS.values());
    }

    public static boolean isRegistrationsLocked() {
        return registrationsLocked;
    }

    public static synchronized void lockRegistrations() {
        if (registrationsLocked) {
            return;
        }
        registrationsLocked = true;
        for (NetworkChannel channel : CHANNELS.values()) {
            channel.freeze();
        }
    }

    public static void setClientSender(Consumer<CustomPacketPayload> sender) {
        clientSender = Objects.requireNonNull(sender, "sender");
    }

    public static void setClientRegistryAccess(Supplier<RegistryAccess> access) {
        clientRegistryAccess = Objects.requireNonNull(access, "access");
    }

    public static void setClientTransportCheck(Predicate<CustomPacketPayload.Type<?>> check) {
        clientTransportCheck = Objects.requireNonNull(check, "check");
    }

    public static RegistryAccess clientRegistryAccess() {
        return clientRegistryAccess.get();
    }

    public static NetworkHandshake.Negotiation applyClientHandshake(NetworkHandshake.ServerHello hello) {
        NetworkHandshake.Negotiation negotiation = NetworkHandshake.acceptServerHello(hello, CHANNELS.values());
        clientServerChannels = Set.copyOf(negotiation.supportedChannels());
        clientMissingRequiredChannels = Set.copyOf(negotiation.missingRequiredChannels());
        clientHandshakeAccepted = negotiation.accepted();
        clientHandshakeMessage = negotiation.message();
        clientHandshakeComplete = true;
        return negotiation;
    }

    public static void resetClientHandshake() {
        clientServerChannels = Set.of();
        clientMissingRequiredChannels = Set.of();
        clientHandshakeAccepted = false;
        clientHandshakeComplete = false;
        clientHandshakeMessage = "";
    }

    public static boolean isClientHandshakeComplete() {
        return clientHandshakeComplete;
    }

    public static boolean isClientHandshakeAccepted() {
        return clientHandshakeAccepted;
    }

    public static Set<ResourceLocation> clientServerChannels() {
        return clientServerChannels;
    }

    public static Set<ResourceLocation> clientMissingRequiredChannels() {
        return clientMissingRequiredChannels;
    }

    public static String clientHandshakeMessage() {
        return clientHandshakeMessage;
    }

    public static boolean canSendToServer(NetworkChannel channel) {
        return clientHandshakeComplete
                && clientHandshakeAccepted
                && clientServerChannels.contains(channel.id());
    }

    public static void sendToServer(
            NetworkChannel channel,
            CustomPacketPayload.Type<?> payloadType,
            CustomPacketPayload payload
    ) {
        requireClientHandshake(channel);
        if (!clientTransportCheck.test(payloadType)) {
            throw new IllegalStateException("The server cannot receive " + payloadType.id());
        }
        clientSender.accept(payload);
    }

    public static void sendBundleToServer(NetworkChannel channel, NetworkBundle bundle) {
        requireClientHandshake(channel);
        if (!clientTransportCheck.test(NetworkBundle.TYPE)) {
            throw new IllegalStateException("The server cannot receive Gemini network bundles");
        }
        clientSender.accept(bundle);
    }

    private static void requireClientHandshake(NetworkChannel channel) {
        if (!clientHandshakeComplete) {
            throw new IllegalStateException("Gemini network handshake has not completed");
        }
        if (!clientHandshakeAccepted) {
            String detail = clientMissingRequiredChannels.isEmpty()
                    ? clientHandshakeMessage
                    : "missing channels " + join(clientMissingRequiredChannels);
            throw new IllegalStateException("Gemini network negotiation failed: " + detail);
        }
        if (!clientServerChannels.contains(channel.id())) {
            if (channel.isRequired()) {
                throw new IllegalStateException("Server does not support required channel " + channel.id());
            }
            throw new IllegalStateException("Server does not support optional channel " + channel.id());
        }
    }

    public static void dispatchBundle(NetworkBundle bundle, ServerPlayer sender) {
        RegistryAccess access = sender.serverLevel().registryAccess();
        for (NetworkBundle.Entry entry : bundle.entries()) {
            NetworkChannel channel = CHANNELS.get(entry.channel());
            if (channel == null) {
                Gemini.LOGGER.warn("Ignoring bundle entry for unknown channel {}", entry.channel());
                continue;
            }
            try {
                channel.dispatchServerbound(entry.path(), entry.data(), sender, access);
            } catch (RuntimeException exception) {
                Gemini.LOGGER.error(
                        "Failed to dispatch serverbound bundle entry {}/{}",
                        entry.channel(),
                        entry.path(),
                        exception
                );
            }
        }
    }

    public static void dispatchClientBundle(NetworkBundle bundle) {
        RegistryAccess access = clientRegistryAccess();
        for (NetworkBundle.Entry entry : bundle.entries()) {
            NetworkChannel channel = CHANNELS.get(entry.channel());
            if (channel == null) {
                Gemini.LOGGER.warn("Ignoring bundle entry for unknown channel {}", entry.channel());
                continue;
            }
            try {
                channel.dispatchClientbound(entry.path(), entry.data(), access);
            } catch (RuntimeException exception) {
                Gemini.LOGGER.error(
                        "Failed to dispatch clientbound bundle entry {}/{}",
                        entry.channel(),
                        entry.path(),
                        exception
                );
            }
        }
    }

    public static boolean canSendTo(ServerPlayer player, NetworkChannel channel) {
        Set<ResourceLocation> supported = SUPPORTED_CHANNELS.get(player.getUUID());
        return supported != null && supported.contains(channel.id());
    }

    public static boolean canSendTo(
            ServerPlayer player,
            NetworkChannel channel,
            CustomPacketPayload.Type<?> payloadType
    ) {
        return canSendTo(player, channel) && ServerPlayNetworking.canSend(player, payloadType);
    }

    public static boolean canSendBundleTo(ServerPlayer player, NetworkChannel channel) {
        return canSendTo(player, channel) && ServerPlayNetworking.canSend(player, NetworkBundle.TYPE);
    }

    private static void handleHello(NetworkHandshake.ClientHello hello, ServerPlayer player) {
        lockRegistrations();
        NetworkHandshake.Negotiation negotiation = NetworkHandshake.acceptClientHello(hello, CHANNELS.values());
        if (!negotiation.accepted()) {
            player.connection.disconnect(Component.literal("Gemini network mismatch: " + negotiation.message()));
            return;
        }
        SUPPORTED_CHANNELS.put(player.getUUID(), Set.copyOf(negotiation.supportedChannels()));
        List<ResourceLocation> serverChannels = CHANNELS.keySet().stream()
                .sorted((left, right) -> left.toString().compareTo(right.toString()))
                .toList();
        ServerPlayNetworking.send(player, new NetworkHandshake.ResponsePayload(
                new NetworkHandshake.ServerHello(
                        NetworkHandshake.PROTOCOL_VERSION,
                        true,
                        serverChannels,
                        ""
                )
        ));
    }

    private static String join(Collection<ResourceLocation> channels) {
        return channels.stream()
                .map(ResourceLocation::toString)
                .sorted()
                .reduce((left, right) -> left + ", " + right)
                .orElse("none");
    }
}

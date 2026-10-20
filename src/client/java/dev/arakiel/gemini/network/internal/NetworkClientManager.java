package dev.arakiel.gemini.network.internal;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.network.api.NetworkChannel;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class NetworkClientManager {
    private NetworkClientManager() {
    }

    public static void init() {
        NetworkManager.setClientSender(ClientPlayNetworking::send);
        NetworkManager.setClientTransportCheck(ClientPlayNetworking::canSend);
        NetworkManager.setClientRegistryAccess(() -> Minecraft.getInstance().level == null
                ? RegistryAccess.EMPTY
                : Minecraft.getInstance().level.registryAccess());
        for (NetworkChannel channel : NetworkManager.channels()) {
            for (NetworkChannel.ClientRegistration<?> registration : channel.clientRegistrations()) {
                registerUnchecked(registration);
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(
                NetworkHandshake.ResponsePayload.TYPE,
                (payload, context) -> {
                    NetworkHandshake.Negotiation negotiation = NetworkManager.applyClientHandshake(payload.data());
                    if (negotiation.accepted()) {
                        return;
                    }
                    Gemini.LOGGER.error("Gemini network negotiation failed: {}", negotiation.message());
                    Minecraft client = context.client();
                    client.execute(() -> {
                        ClientPacketListener connection = client.getConnection();
                        if (connection != null) {
                            connection.getConnection().disconnect(Component.literal(
                                    "Gemini network mismatch: " + negotiation.message()
                            ));
                        }
                    });
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(
                NetworkBundle.TYPE,
                (payload, context) -> NetworkManager.dispatchClientBundle(payload)
        );
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            List<NetworkChannel> channels = List.copyOf(NetworkManager.channels());
            List<ResourceLocation> channelIds = channels.stream()
                    .map(NetworkChannel::id)
                    .toList();
            List<ResourceLocation> requiredChannels = channels.stream()
                    .filter(NetworkChannel::isRequired)
                    .map(NetworkChannel::id)
                    .toList();
            NetworkManager.resetClientHandshake();
            NetworkManager.lockRegistrations();
            ClientPlayNetworking.send(new NetworkHandshake.HelloPayload(
                    new NetworkHandshake.ClientHello(
                            NetworkHandshake.PROTOCOL_VERSION,
                            channelIds,
                            requiredChannels
                    )
            ));
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> NetworkManager.resetClientHandshake());
    }

    public static boolean canSendToServer(NetworkChannel channel, Class<?> messageClass) {
        return NetworkManager.canSendToServer(channel)
                && channel.isServerbound(messageClass)
                && ClientPlayNetworking.canSend(channel.payloadType(messageClass));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerUnchecked(NetworkChannel.ClientRegistration<?> registration) {
        ClientPlayNetworking.registerGlobalReceiver(
                (net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type) registration.payloadType(),
                (payload, context) -> {
                    NetworkChannel.MessagePayload message = (NetworkChannel.MessagePayload) payload;
                    try {
                        ((java.util.function.Consumer) registration.handler()).accept(message.message());
                    } catch (RuntimeException exception) {
                        Gemini.LOGGER.error(
                                "Network handler failed for clientbound message {}",
                                registration.payloadType().id(),
                                exception
                        );
                    }
                }
        );
    }
}

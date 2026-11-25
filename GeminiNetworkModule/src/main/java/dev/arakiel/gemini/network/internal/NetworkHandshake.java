package dev.arakiel.gemini.network.internal;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.network.NetworkChannel;
import dev.arakiel.gemini.network.RecordCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class NetworkHandshake {
    public static final int PROTOCOL_VERSION = 2;

    private NetworkHandshake() {
    }

    public record ClientHello(int version, List<ResourceLocation> channels, List<ResourceLocation> requiredChannels) {
        public ClientHello {
            channels = List.copyOf(channels);
            requiredChannels = List.copyOf(requiredChannels);
        }
    }

    public record ServerHello(int version, boolean accepted, List<ResourceLocation> channels, String message) {
        public ServerHello {
            channels = List.copyOf(channels);
            message = Objects.requireNonNull(message, "message");
        }
    }

    public record Negotiation(
            boolean accepted,
            List<ResourceLocation> supportedChannels,
            List<ResourceLocation> missingRequiredChannels,
            String message
    ) {
        public Negotiation {
            supportedChannels = List.copyOf(supportedChannels);
            missingRequiredChannels = List.copyOf(missingRequiredChannels);
            message = Objects.requireNonNull(message, "message");
        }
    }

    public static Negotiation acceptClientHello(ClientHello hello, Collection<NetworkChannel> serverChannels) {
        Objects.requireNonNull(hello, "hello");
        Objects.requireNonNull(serverChannels, "serverChannels");

        Set<ResourceLocation> serverIds = channelIds(serverChannels);
        Set<ResourceLocation> advertised = new HashSet<>(hello.channels());
        Set<ResourceLocation> missingOnClient = new LinkedHashSet<>();
        for (NetworkChannel channel : serverChannels) {
            if (channel.isRequired() && !advertised.contains(channel.id())) {
                missingOnClient.add(channel.id());
            }
        }

        Set<ResourceLocation> missingOnServer = new LinkedHashSet<>(hello.requiredChannels());
        missingOnServer.removeAll(serverIds);
        Set<ResourceLocation> missing = new LinkedHashSet<>(missingOnClient);
        missing.addAll(missingOnServer);
        Set<ResourceLocation> supported = new HashSet<>(serverIds);
        supported.retainAll(advertised);

        if (hello.version() != PROTOCOL_VERSION) {
            return new Negotiation(
                    false,
                    sorted(supported),
                    sorted(missing),
                    "Gemini network protocol mismatch: client=" + hello.version()
                            + ", server=" + PROTOCOL_VERSION
            );
        }
        if (!missing.isEmpty()) {
            List<String> parts = new ArrayList<>();
            if (!missingOnClient.isEmpty()) {
                parts.add("client is missing " + join(missingOnClient));
            }
            if (!missingOnServer.isEmpty()) {
                parts.add("server is missing " + join(missingOnServer));
            }
            return new Negotiation(false, sorted(supported), sorted(missing), String.join("; ", parts));
        }
        return new Negotiation(true, sorted(supported), List.of(), "");
    }

    public static Negotiation acceptServerHello(ServerHello hello, Collection<NetworkChannel> clientChannels) {
        Objects.requireNonNull(hello, "hello");
        Objects.requireNonNull(clientChannels, "clientChannels");

        Set<ResourceLocation> serverIds = new HashSet<>(hello.channels());
        Set<ResourceLocation> clientIds = channelIds(clientChannels);
        Set<ResourceLocation> missing = new LinkedHashSet<>();
        for (NetworkChannel channel : clientChannels) {
            if (channel.isRequired() && !serverIds.contains(channel.id())) {
                missing.add(channel.id());
            }
        }
        Set<ResourceLocation> supported = new HashSet<>(clientIds);
        supported.retainAll(serverIds);

        if (hello.version() != PROTOCOL_VERSION) {
            return new Negotiation(
                    false,
                    sorted(supported),
                    sorted(missing),
                    "Gemini network protocol mismatch: server=" + hello.version()
                            + ", client=" + PROTOCOL_VERSION
            );
        }
        if (!hello.accepted()) {
            return new Negotiation(
                    false,
                    sorted(supported),
                    sorted(missing),
                    hello.message().isBlank() ? "Gemini network negotiation was rejected" : hello.message()
            );
        }
        if (!missing.isEmpty()) {
            return new Negotiation(
                    false,
                    sorted(supported),
                    sorted(missing),
                    "Server is missing required Gemini channels: " + join(missing)
            );
        }
        return new Negotiation(true, sorted(supported), List.of(), "");
    }

    private static Set<ResourceLocation> channelIds(Collection<NetworkChannel> channels) {
        Set<ResourceLocation> ids = new HashSet<>();
        for (NetworkChannel channel : channels) {
            ids.add(channel.id());
        }
        return ids;
    }

    private static List<ResourceLocation> sorted(Collection<ResourceLocation> channels) {
        return channels.stream()
                .sorted((left, right) -> left.toString().compareTo(right.toString()))
                .toList();
    }

    private static String join(Collection<ResourceLocation> channels) {
        return channels.stream()
                .map(ResourceLocation::toString)
                .sorted()
                .reduce((left, right) -> left + ", " + right)
                .orElse("none");
    }

    public record HelloPayload(ClientHello data) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<HelloPayload> TYPE =
                new CustomPacketPayload.Type<>(Gemini.id("handshake/hello"));
        public static final StreamCodec<RegistryFriendlyByteBuf, HelloPayload> CODEC = StreamCodec.of(
                (buffer, payload) -> RecordCodecs.create(ClientHello.class).encode(buffer, payload.data()),
                buffer -> new HelloPayload(RecordCodecs.create(ClientHello.class).decode(buffer))
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ResponsePayload(ServerHello data) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ResponsePayload> TYPE =
                new CustomPacketPayload.Type<>(Gemini.id("handshake/response"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ResponsePayload> CODEC = StreamCodec.of(
                (buffer, payload) -> RecordCodecs.create(ServerHello.class).encode(buffer, payload.data()),
                buffer -> new ResponsePayload(RecordCodecs.create(ServerHello.class).decode(buffer))
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}

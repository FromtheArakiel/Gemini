package dev.arakiel.gemini.network;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.network.internal.NetworkBundle;
import dev.arakiel.gemini.network.internal.NetworkManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class NetworkChannel {
    private final ResourceLocation id;
    private final Map<Class<?>, MessageType<?>> messageTypes = new HashMap<>();
    private final Map<String, MessageType<?>> messageTypesByPath = new HashMap<>();
    private final List<ClientRegistration<?>> clientRegistrations = new ArrayList<>();
    private volatile boolean required;
    private volatile boolean frozen;

    public NetworkChannel(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation id() {
        return id;
    }

    public synchronized NetworkChannel required() {
        ensureOpen();
        this.required = true;
        return this;
    }

    public synchronized NetworkChannel optional() {
        ensureOpen();
        this.required = false;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public synchronized <T> void registerClientbound(
            String path,
            Class<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            Consumer<T> handler
    ) {
        MessageType<T> messageType = registration(path, type, codec);
        if (messageType.clientHandler != null) {
            throw new IllegalStateException("Clientbound message already registered: " + type.getName());
        }
        messageType.clientHandler = Objects.requireNonNull(handler, "handler");
        messageType.clientbound = true;
        PayloadTypeRegistry.playS2C().register(messageType.payloadType, payloadCodec(messageType));
        clientRegistrations.add(new ClientRegistration<>(messageType.payloadType, handler));
    }

    public synchronized <T> void registerClientbound(
            Class<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            Consumer<T> handler
    ) {
        registerClientbound(defaultPath(type), type, codec, handler);
    }

    public synchronized <T> void registerServerbound(
            String path,
            Class<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, ServerPlayer> handler
    ) {
        MessageType<T> messageType = registration(path, type, codec);
        if (messageType.serverHandler != null) {
            throw new IllegalStateException("Serverbound message already registered: " + type.getName());
        }
        messageType.serverHandler = Objects.requireNonNull(handler, "handler");
        messageType.serverbound = true;
        PayloadTypeRegistry.playC2S().register(messageType.payloadType, payloadCodec(messageType));
        ServerPlayNetworking.registerGlobalReceiver(
                messageType.payloadType,
                (payload, context) -> invokeServerHandler(messageType, payload.message(), context.player())
        );
    }

    public synchronized <T> void registerServerbound(
            Class<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, ServerPlayer> handler
    ) {
        registerServerbound(defaultPath(type), type, codec, handler);
    }

    public <T> void sendToServer(T message) {
        MessageType<T> type = messageType(message);
        requireDirection(type.clientbound, type.serverbound, Direction.SERVERBOUND, message.getClass());
        NetworkManager.sendToServer(this, type.payloadType, new MessagePayload<>(type.payloadType, message));
    }

    public <T> void sendTo(ServerPlayer player, T message) {
        MessageType<T> type = messageType(message);
        requireDirection(type.clientbound, type.serverbound, Direction.CLIENTBOUND, message.getClass());
        sendClientbound(player, type, new MessagePayload<>(type.payloadType, message));
    }

    public <T> void sendToAll(MinecraftServer server, T message) {
        MessageType<T> type = messageType(message);
        requireDirection(type.clientbound, type.serverbound, Direction.CLIENTBOUND, message.getClass());
        MessagePayload<T> payload = new MessagePayload<>(type.payloadType, message);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendClientbound(player, type, payload);
        }
    }

    public <T> void sendToAllExcept(MinecraftServer server, ServerPlayer excluded, T message) {
        MessageType<T> type = messageType(message);
        requireDirection(type.clientbound, type.serverbound, Direction.CLIENTBOUND, message.getClass());
        MessagePayload<T> payload = new MessagePayload<>(type.payloadType, message);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player != excluded) {
                sendClientbound(player, type, payload);
            }
        }
    }

    public <T> void sendToPlayers(Iterable<ServerPlayer> players, T message) {
        MessageType<T> type = messageType(message);
        requireDirection(type.clientbound, type.serverbound, Direction.CLIENTBOUND, message.getClass());
        MessagePayload<T> payload = new MessagePayload<>(type.payloadType, message);
        for (ServerPlayer player : players) {
            sendClientbound(player, type, payload);
        }
    }

    public <T> void sendToDimension(ServerLevel level, T message) {
        sendToPlayers(level.players(), message);
    }

    public <T> void sendToNear(ServerLevel level, Vec3 center, double radius, T message) {
        MessageType<T> type = messageType(message);
        requireDirection(type.clientbound, type.serverbound, Direction.CLIENTBOUND, message.getClass());
        validateRadius(radius);
        MessagePayload<T> payload = new MessagePayload<>(type.payloadType, message);
        double radiusSqr = radius * radius;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(center) <= radiusSqr) {
                sendClientbound(player, type, payload);
            }
        }
    }

    public <T> void sendToNear(ServerLevel level, Entity center, double radius, T message) {
        sendToNear(level, center.position(), radius, message);
    }

    public <T> void sendToTracking(Entity entity, T message) {
        sendToTracking(entity, false, message);
    }

    public <T> void sendToTrackingAndSelf(Entity entity, T message) {
        sendToTracking(entity, true, message);
    }

    private <T> void sendToTracking(Entity entity, boolean includeSelf, T message) {
        MessageType<T> type = messageType(message);
        requireDirection(type.clientbound, type.serverbound, Direction.CLIENTBOUND, message.getClass());
        Set<ServerPlayer> players = new LinkedHashSet<>(PlayerLookup.tracking(entity));
        if (includeSelf && entity instanceof ServerPlayer player) {
            players.add(player);
        }
        MessagePayload<T> payload = new MessagePayload<>(type.payloadType, message);
        for (ServerPlayer player : players) {
            sendClientbound(player, type, payload);
        }
    }

    public void sendBundleToServer(List<?> messages) {
        List<NetworkBundle.Entry> entries = encodeEntries(
                messages,
                NetworkManager.clientRegistryAccess(),
                Direction.SERVERBOUND
        );
        for (NetworkBundle bundle : NetworkBundle.partition(entries)) {
            NetworkManager.sendBundleToServer(this, bundle);
        }
    }

    public void sendBundle(ServerPlayer player, List<?> messages) {
        List<NetworkBundle.Entry> entries = encodeEntries(
                messages,
                player.serverLevel().registryAccess(),
                Direction.CLIENTBOUND
        );
        for (NetworkBundle bundle : NetworkBundle.partition(entries)) {
            if (NetworkManager.canSendBundleTo(player, this)) {
                ServerPlayNetworking.send(player, bundle);
            }
        }
    }

    public void sendBundleToAll(MinecraftServer server, List<?> messages) {
        sendBundlesToPlayers(server.getPlayerList().getPlayers(), messages, server.registryAccess());
    }

    public void sendBundleToAllExcept(MinecraftServer server, ServerPlayer excluded, List<?> messages) {
        List<ServerPlayer> players = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player != excluded) {
                players.add(player);
            }
        }
        sendBundlesToPlayers(players, messages, server.registryAccess());
    }

    public void sendBundleToPlayers(Collection<ServerPlayer> players, List<?> messages, RegistryAccess access) {
        sendBundlesToPlayers(players, messages, access);
    }

    public void sendBundleToDimension(ServerLevel level, List<?> messages) {
        sendBundlesToPlayers(level.players(), messages, level.registryAccess());
    }

    public void sendBundleToNear(ServerLevel level, Vec3 center, double radius, List<?> messages) {
        validateRadius(radius);
        double radiusSqr = radius * radius;
        List<ServerPlayer> players = new ArrayList<>();
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(center) <= radiusSqr) {
                players.add(player);
            }
        }
        sendBundlesToPlayers(players, messages, level.registryAccess());
    }

    public void sendBundleToTracking(Entity entity, List<?> messages) {
        sendBundleToTracking(entity, false, messages);
    }

    public void sendBundleToTrackingAndSelf(Entity entity, List<?> messages) {
        sendBundleToTracking(entity, true, messages);
    }

    private void sendBundleToTracking(Entity entity, boolean includeSelf, List<?> messages) {
        Set<ServerPlayer> players = new LinkedHashSet<>(PlayerLookup.tracking(entity));
        if (includeSelf && entity instanceof ServerPlayer player) {
            players.add(player);
        }
        sendBundlesToPlayers(players, messages, entity.level().registryAccess());
    }

    private void sendBundlesToPlayers(Collection<ServerPlayer> players, List<?> messages, RegistryAccess access) {
        List<NetworkBundle.Entry> entries = encodeEntries(messages, access, Direction.CLIENTBOUND);
        List<NetworkBundle> bundles = NetworkBundle.partition(entries);
        for (ServerPlayer player : players) {
            if (!NetworkManager.canSendBundleTo(player, this)) {
                continue;
            }
            for (NetworkBundle bundle : bundles) {
                ServerPlayNetworking.send(player, bundle);
            }
        }
    }

    List<NetworkBundle.Entry> encodeEntries(List<?> messages, RegistryAccess access, Direction direction) {
        Objects.requireNonNull(messages, "messages");
        Objects.requireNonNull(access, "access");
        List<NetworkBundle.Entry> entries = new ArrayList<>(messages.size());
        for (Object message : messages) {
            MessageType<Object> type = messageType(Objects.requireNonNull(message, "message").getClass());
            requireDirection(type.clientbound, type.serverbound, direction, message.getClass());
            RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), access);
            try {
                type.codec.encode(buffer, message);
                byte[] data = new byte[buffer.readableBytes()];
                buffer.readBytes(data);
                entries.add(new NetworkBundle.Entry(id, type.path, data));
            } finally {
                buffer.release();
            }
        }
        return entries;
    }

    public List<ClientRegistration<?>> clientRegistrations() {
        return List.copyOf(clientRegistrations);
    }

    public CustomPacketPayload.Type<?> payloadType(Class<?> messageClass) {
        return messageType(messageClass).payloadType;
    }

    public boolean isClientbound(Class<?> messageClass) {
        return messageType(messageClass).clientbound;
    }

    public boolean isServerbound(Class<?> messageClass) {
        return messageType(messageClass).serverbound;
    }

    public boolean canSendTo(ServerPlayer player, Class<?> messageClass) {
        MessageType<?> type = messageType(messageClass);
        return type.clientbound && NetworkManager.canSendTo(player, this, type.payloadType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void dispatchServerbound(String path, byte[] data, ServerPlayer sender, RegistryAccess access) {
        MessageType<?> type = messageTypesByPath.get(path);
        if (type == null || type.serverHandler == null || !type.serverbound) {
            throw new IllegalArgumentException("No serverbound handler for " + id + "/" + path);
        }
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), access);
        Object message;
        try {
            message = ((StreamCodec) type.codec).decode(buffer);
            ensureFullyRead(buffer, id, path);
        } finally {
            buffer.release();
        }
        ((BiConsumer) type.serverHandler).accept(message, sender);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void dispatchClientbound(String path, byte[] data, RegistryAccess access) {
        MessageType<?> type = messageTypesByPath.get(path);
        if (type == null || type.clientHandler == null || !type.clientbound) {
            throw new IllegalArgumentException("No clientbound handler for " + id + "/" + path);
        }
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), access);
        Object message;
        try {
            message = ((StreamCodec) type.codec).decode(buffer);
            ensureFullyRead(buffer, id, path);
        } finally {
            buffer.release();
        }
        ((Consumer) type.clientHandler).accept(message);
    }

    public synchronized void freeze() {
        frozen = true;
    }

    private static void ensureFullyRead(RegistryFriendlyByteBuf buffer, ResourceLocation channel, String path) {
        if (buffer.isReadable()) {
            throw new IllegalArgumentException(
                    "Trailing bytes for " + channel + "/" + path + ": " + buffer.readableBytes()
            );
        }
    }

    private <T> void sendClientbound(ServerPlayer player, MessageType<T> type, MessagePayload<T> payload) {
        if (NetworkManager.canSendTo(player, this, type.payloadType)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized <T> MessageType<T> registration(
            String path,
            Class<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        ensureOpen();
        validateMessagePath(path);
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(codec, "codec");
        MessageType<?> existing = messageTypes.get(type);
        if (existing != null) {
            if (!existing.path.equals(path)) {
                throw new IllegalStateException("Message registered under a different path: " + type.getName());
            }
            return (MessageType<T>) existing;
        }
        MessageType<?> existingPath = messageTypesByPath.get(path);
        if (existingPath != null) {
            throw new IllegalStateException("Message path already registered: " + path);
        }
        ResourceLocation payloadId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "/" + path);
        MessageType<T> created = new MessageType<>(
                path,
                codec,
                new CustomPacketPayload.Type<>(payloadId)
        );
        messageTypes.put(type, created);
        messageTypesByPath.put(path, created);
        return created;
    }

    @SuppressWarnings("unchecked")
    private <T> MessageType<T> messageType(T message) {
        Objects.requireNonNull(message, "message");
        return messageType(message.getClass());
    }

    @SuppressWarnings("unchecked")
    private <T> MessageType<T> messageType(Class<?> messageClass) {
        Objects.requireNonNull(messageClass, "messageClass");
        MessageType<?> type = messageTypes.get(messageClass);
        if (type == null) {
            throw new IllegalArgumentException("Unregistered message type: " + messageClass.getName());
        }
        return (MessageType<T>) type;
    }

    private void ensureOpen() {
        if (frozen) {
            throw new IllegalStateException("Channel registrations are frozen: " + id);
        }
    }

    static String defaultPath(Class<?> type) {
        String simpleName = type.getSimpleName();
        if (simpleName.isEmpty()) {
            simpleName = type.getName();
        }
        StringBuilder result = new StringBuilder(simpleName.length() + 4);
        for (int i = 0; i < simpleName.length(); i++) {
            char character = simpleName.charAt(i);
            if (Character.isUpperCase(character)) {
                if (i > 0 && !result.isEmpty() && result.charAt(result.length() - 1) != '_') {
                    result.append('_');
                }
                result.append(Character.toLowerCase(character));
            } else if (Character.isLetterOrDigit(character)
                    || character == '_' || character == '-' || character == '.') {
                result.append(Character.toLowerCase(character));
            } else if (!result.isEmpty() && result.charAt(result.length() - 1) != '_') {
                result.append('_');
            }
        }
        if (result.isEmpty()) {
            return "message";
        }
        if (result.length() > 64) {
            result.setLength(64);
        }
        if (result.charAt(0) == '_' || result.charAt(0) == '-') {
            result.insert(0, 'm');
        }
        return result.toString();
    }

    static void validateMessagePath(String path) {
        Objects.requireNonNull(path, "path");
        if (path.isBlank() || path.length() > 64) {
            throw new IllegalArgumentException("Invalid message path: " + path);
        }
        for (int i = 0; i < path.length(); i++) {
            char character = path.charAt(i);
            boolean valid = character >= 'a' && character <= 'z'
                    || character >= '0' && character <= '9'
                    || character == '_' || character == '-' || character == '.';
            if (!valid) {
                throw new IllegalArgumentException("Invalid message path: " + path);
            }
        }
    }

    static void requireDirection(boolean clientbound, boolean serverbound, Direction expected, Class<?> type) {
        boolean registered = expected == Direction.CLIENTBOUND ? clientbound : serverbound;
        if (!registered) {
            throw new IllegalStateException(
                    "Message is not registered as " + expected.name().toLowerCase() + ": " + type.getName()
            );
        }
    }

    private static void validateRadius(double radius) {
        if (radius < 0.0D || Double.isNaN(radius)) {
            throw new IllegalArgumentException("Radius must be non-negative: " + radius);
        }
    }

    private static <T> void invokeServerHandler(MessageType<T> type, T message, ServerPlayer player) {
        try {
            type.serverHandler.accept(message, player);
        } catch (RuntimeException exception) {
            Gemini.LOGGER.error(
                    "Network handler failed for serverbound {} message {}",
                    type.path,
                    type.payloadType.id(),
                    exception
            );
        }
    }

    private static <T> StreamCodec<RegistryFriendlyByteBuf, MessagePayload<T>> payloadCodec(MessageType<T> type) {
        return StreamCodec.of(
                (buffer, payload) -> type.codec.encode(buffer, payload.message()),
                buffer -> new MessagePayload<>(type.payloadType, type.codec.decode(buffer))
        );
    }

    enum Direction {
        CLIENTBOUND,
        SERVERBOUND
    }

    private static final class MessageType<T> {
        private final String path;
        private final StreamCodec<RegistryFriendlyByteBuf, T> codec;
        private final CustomPacketPayload.Type<MessagePayload<T>> payloadType;
        private Consumer<T> clientHandler;
        private BiConsumer<T, ServerPlayer> serverHandler;
        private boolean clientbound;
        private boolean serverbound;

        private MessageType(
                String path,
                StreamCodec<RegistryFriendlyByteBuf, T> codec,
                CustomPacketPayload.Type<MessagePayload<T>> payloadType
        ) {
            this.path = path;
            this.codec = codec;
            this.payloadType = payloadType;
        }
    }

    public record MessagePayload<T>(CustomPacketPayload.Type<MessagePayload<T>> payloadType, T message)
            implements CustomPacketPayload {
        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return payloadType;
        }
    }

    public record ClientRegistration<T>(CustomPacketPayload.Type<MessagePayload<T>> payloadType, Consumer<T> handler) {
    }
}

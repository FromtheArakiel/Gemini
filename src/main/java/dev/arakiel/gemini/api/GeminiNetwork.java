package dev.arakiel.gemini.api;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.network.api.NetworkChannel;
import dev.arakiel.gemini.network.internal.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Set;

public final class GeminiNetwork {
    private GeminiNetwork() {
    }

    public static void init() {
        NetworkManager.init();
    }

    public static NetworkChannel channel(String path) {
        return NetworkManager.channel(Gemini.id(path));
    }

    public static NetworkChannel channel(ResourceLocation id) {
        return NetworkManager.channel(id);
    }

    public static NetworkChannel getChannel(ResourceLocation id) {
        return NetworkManager.getChannel(id);
    }

    public static Collection<NetworkChannel> channels() {
        return NetworkManager.channels();
    }

    public static boolean isRegistrationsLocked() {
        return NetworkManager.isRegistrationsLocked();
    }

    public static boolean canSendTo(ServerPlayer player, NetworkChannel channel) {
        return NetworkManager.canSendTo(player, channel);
    }

    public static boolean isClientHandshakeComplete() {
        return NetworkManager.isClientHandshakeComplete();
    }

    public static boolean isClientHandshakeAccepted() {
        return NetworkManager.isClientHandshakeAccepted();
    }

    public static boolean canSendToServer(NetworkChannel channel) {
        return NetworkManager.canSendToServer(channel);
    }

    public static Set<ResourceLocation> clientServerChannels() {
        return NetworkManager.clientServerChannels();
    }

    public static Set<ResourceLocation> clientMissingRequiredChannels() {
        return NetworkManager.clientMissingRequiredChannels();
    }

    public static String clientHandshakeMessage() {
        return NetworkManager.clientHandshakeMessage();
    }
}

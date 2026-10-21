package dev.arakiel.gemini.network.client;

import dev.arakiel.gemini.network.GeminiNetwork;
import dev.arakiel.gemini.network.NetworkChannel;
import dev.arakiel.gemini.network.client.internal.NetworkClientManager;

public final class GeminiNetworkClient {
    private GeminiNetworkClient() {
    }

    public static void init() {
        NetworkClientManager.init();
    }

    public static boolean canSendToServer(NetworkChannel channel, Class<?> messageClass) {
        return NetworkClientManager.canSendToServer(channel, messageClass);
    }

    public static boolean isHandshakeComplete() {
        return GeminiNetwork.isClientHandshakeComplete();
    }

    public static boolean isHandshakeAccepted() {
        return GeminiNetwork.isClientHandshakeAccepted();
    }
}

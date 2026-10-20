package dev.arakiel.gemini.api;

import dev.arakiel.gemini.network.api.NetworkChannel;
import dev.arakiel.gemini.network.internal.NetworkClientManager;

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

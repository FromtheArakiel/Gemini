package dev.arakiel.gemini.component.api;

import dev.arakiel.gemini.component.internal.network.C2SMessagingClient;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface ClientComponentSync extends ComponentSync.SelfMessaging {
    default void sendC2SMessage(ComponentSync.C2SPacketWriter writer) {
        C2SMessagingClient.send(this, writer);
    }

    static void sendC2SMessage(ComponentKey<?> key, ComponentSync.C2SPacketWriter writer) {
        C2SMessagingClient.send(key, writer);
    }
}

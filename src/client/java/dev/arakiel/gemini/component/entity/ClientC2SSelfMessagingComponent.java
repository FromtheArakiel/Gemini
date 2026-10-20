package dev.arakiel.gemini.component.entity;

import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.sync.C2SComponentPacketWriter;
import dev.arakiel.gemini.component.internal.network.C2SMessagingClient;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface ClientC2SSelfMessagingComponent extends C2SSelfMessagingComponent {
    default void sendC2SMessage(C2SComponentPacketWriter writer) {
        C2SMessagingClient.send(this, writer);
    }

    static void sendC2SMessage(ComponentKey<?> key, C2SComponentPacketWriter writer) {
        C2SMessagingClient.send(key, writer);
    }
}

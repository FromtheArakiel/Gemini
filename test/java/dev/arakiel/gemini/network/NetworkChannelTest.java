package dev.arakiel.gemini.network;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkChannelTest {
    @Test
    void channelIdsAreStableAndUnique() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("gemini_test", "channel_a");
        NetworkChannel first = GeminiNetwork.channel(id);
        NetworkChannel second = GeminiNetwork.channel(id);

        assertSame(first, second);
        assertEquals(id, first.id());
    }

    @Test
    void unregisteredMessagesAreRejected() {
        NetworkChannel channel = GeminiNetwork.channel(
                ResourceLocation.fromNamespaceAndPath("gemini_test", "channel_b")
        );

        assertThrows(IllegalArgumentException.class, () -> channel.sendToServer("missing"));
    }

    @Test
    void defaultPathsBecomeValidResourceLocations() {
        assertEquals("foo_bar_packet", NetworkChannel.defaultPath(FooBarPacket.class));
        assertEquals("g2_s_packet", NetworkChannel.defaultPath(G2SPacket.class));
        assertEquals("symbols_only", NetworkChannel.defaultPath(SymbolsOnly.class));
    }

    @Test
    void messagePathsRejectUnsafeCharacters() {
        assertThrows(IllegalArgumentException.class, () -> NetworkChannel.validateMessagePath("Upper"));
        assertThrows(IllegalArgumentException.class, () -> NetworkChannel.validateMessagePath("with space"));
        assertThrows(IllegalArgumentException.class, () -> NetworkChannel.validateMessagePath(""));
    }

    @Test
    void bundleDirectionIsValidated() {
        assertThrows(IllegalStateException.class, () -> NetworkChannel.requireDirection(
                false,
                true,
                NetworkChannel.Direction.CLIENTBOUND,
                FooBarPacket.class
        ));
        assertThrows(IllegalStateException.class, () -> NetworkChannel.requireDirection(
                true,
                false,
                NetworkChannel.Direction.SERVERBOUND,
                FooBarPacket.class
        ));
    }

    @Test
    void frozenChannelsRejectFurtherConfiguration() {
        NetworkChannel channel = new NetworkChannel(
                ResourceLocation.fromNamespaceAndPath("gemini_test", "frozen_" + System.nanoTime())
        );
        channel.freeze();

        assertTrue(channel.isFrozen());
        assertThrows(IllegalStateException.class, channel::required);
    }

    private static final class FooBarPacket {
    }

    private static final class G2SPacket {
    }

    private static final class SymbolsOnly {
    }
}

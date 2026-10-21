package dev.arakiel.gemini.network;

import dev.arakiel.gemini.network.internal.NetworkHandshake;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkHandshakeTest {
    private static final ResourceLocation ALPHA = id("alpha");
    private static final ResourceLocation BETA = id("beta");
    private static final ResourceLocation GAMMA = id("gamma");

    @Test
    void matchingRequiredChannelsAreAccepted() {
        NetworkChannel required = channel(ALPHA, true);
        NetworkChannel optional = channel(BETA, false);

        NetworkHandshake.Negotiation negotiation = NetworkHandshake.acceptClientHello(
                new NetworkHandshake.ClientHello(
                        NetworkHandshake.PROTOCOL_VERSION,
                        List.of(ALPHA, BETA),
                        List.of(ALPHA)
                ),
                List.of(required, optional)
        );

        assertTrue(negotiation.accepted());
        assertEquals(List.of(ALPHA, BETA), negotiation.supportedChannels());
        assertTrue(negotiation.missingRequiredChannels().isEmpty());
    }

    @Test
    void missingRequiredServerChannelIsRejected() {
        NetworkChannel required = channel(ALPHA, true);
        NetworkChannel optional = channel(BETA, false);

        NetworkHandshake.Negotiation negotiation = NetworkHandshake.acceptClientHello(
                new NetworkHandshake.ClientHello(
                        NetworkHandshake.PROTOCOL_VERSION,
                        List.of(BETA),
                        List.of()
                ),
                List.of(required, optional)
        );

        assertFalse(negotiation.accepted());
        assertEquals(List.of(ALPHA), negotiation.missingRequiredChannels());
        assertEquals(List.of(BETA), negotiation.supportedChannels());
        assertTrue(negotiation.message().contains(ALPHA.toString()));
    }

    @Test
    void missingClientRequiredChannelIsRejectedByServer() {
        NetworkChannel optional = channel(BETA, false);

        NetworkHandshake.Negotiation negotiation = NetworkHandshake.acceptClientHello(
                new NetworkHandshake.ClientHello(
                        NetworkHandshake.PROTOCOL_VERSION,
                        List.of(BETA),
                        List.of(ALPHA)
                ),
                List.of(optional)
        );

        assertFalse(negotiation.accepted());
        assertEquals(List.of(ALPHA), negotiation.missingRequiredChannels());
        assertTrue(negotiation.message().contains("server is missing"));
    }

    @Test
    void protocolMismatchIsRejected() {
        NetworkHandshake.Negotiation negotiation = NetworkHandshake.acceptClientHello(
                new NetworkHandshake.ClientHello(
                        NetworkHandshake.PROTOCOL_VERSION + 1,
                        List.of(ALPHA),
                        List.of(ALPHA)
                ),
                List.of(channel(ALPHA, true))
        );

        assertFalse(negotiation.accepted());
        assertTrue(negotiation.message().contains("protocol mismatch"));
    }

    @Test
    void absentOptionalChannelDoesNotBreakNegotiation() {
        NetworkHandshake.Negotiation negotiation = NetworkHandshake.acceptClientHello(
                new NetworkHandshake.ClientHello(
                        NetworkHandshake.PROTOCOL_VERSION,
                        List.of(ALPHA),
                        List.of()
                ),
                List.of(channel(ALPHA, false), channel(BETA, false))
        );

        assertTrue(negotiation.accepted());
        assertEquals(List.of(ALPHA), negotiation.supportedChannels());
        assertFalse(negotiation.supportedChannels().contains(BETA));
    }

    @Test
    void clientRejectsServerWithoutRequiredChannel() {
        NetworkHandshake.Negotiation negotiation = NetworkHandshake.acceptServerHello(
                new NetworkHandshake.ServerHello(
                        NetworkHandshake.PROTOCOL_VERSION,
                        true,
                        List.of(BETA, GAMMA),
                        ""
                ),
                List.of(channel(ALPHA, true), channel(BETA, false))
        );

        assertFalse(negotiation.accepted());
        assertEquals(List.of(ALPHA), negotiation.missingRequiredChannels());
        assertEquals(List.of(BETA), negotiation.supportedChannels());
    }

    @Test
    void clientAcceptsNegotiatedServerChannels() {
        NetworkHandshake.Negotiation negotiation = NetworkHandshake.acceptServerHello(
                new NetworkHandshake.ServerHello(
                        NetworkHandshake.PROTOCOL_VERSION,
                        true,
                        List.of(ALPHA, BETA),
                        ""
                ),
                List.of(channel(ALPHA, true), channel(BETA, false))
        );

        assertTrue(negotiation.accepted());
        assertEquals(List.of(ALPHA, BETA), negotiation.supportedChannels());
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("gemini_test", path);
    }

    private static NetworkChannel channel(ResourceLocation id, boolean required) {
        NetworkChannel channel = new NetworkChannel(id);
        if (required) {
            channel.required();
        }
        return channel;
    }
}

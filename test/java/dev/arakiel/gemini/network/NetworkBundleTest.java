package dev.arakiel.gemini.network;

import dev.arakiel.gemini.network.internal.NetworkBundle;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NetworkBundleTest {
    private static final ResourceLocation CHANNEL =
            ResourceLocation.fromNamespaceAndPath("gemini_test", "bundle");

    @Test
    void bundleRoundTripsEntries() {
        NetworkBundle bundle = new NetworkBundle(List.of(
                new NetworkBundle.Entry(CHANNEL, "alpha", new byte[]{1, 2, 3}),
                new NetworkBundle.Entry(CHANNEL, "beta", new byte[0])
        ));

        RegistryFriendlyByteBuf buffer = newBuffer();
        try {
            NetworkBundle.CODEC.encode(buffer, bundle);
            NetworkBundle decoded = NetworkBundle.CODEC.decode(buffer);
            assertEquals(2, decoded.entries().size());
            assertEquals("alpha", decoded.entries().get(0).path());
            assertArrayEquals(new byte[]{1, 2, 3}, decoded.entries().get(0).data());
            assertEquals("beta", decoded.entries().get(1).path());
            assertArrayEquals(new byte[0], decoded.entries().get(1).data());
        } finally {
            buffer.release();
        }
    }

    @Test
    void oversizedEntryIsRejectedBeforeEncoding() {
        NetworkBundle.Entry entry = new NetworkBundle.Entry(
                CHANNEL,
                "large",
                new byte[NetworkBundle.MAX_ENTRY_SIZE + 1]
        );

        RegistryFriendlyByteBuf buffer = newBuffer();
        try {
            assertThrows(IllegalArgumentException.class,
                    () -> NetworkBundle.CODEC.encode(buffer, new NetworkBundle(List.of(entry))));
        } finally {
            buffer.release();
        }
    }

    @Test
    void oversizedEntryLengthIsRejectedWhileDecoding() {
        RegistryFriendlyByteBuf buffer = newBuffer();
        try {
            buffer.writeVarInt(1);
            buffer.writeResourceLocation(CHANNEL);
            buffer.writeUtf("large");
            buffer.writeVarInt(NetworkBundle.MAX_ENTRY_SIZE + 1);
            assertThrows(IllegalArgumentException.class, () -> NetworkBundle.CODEC.decode(buffer));
        } finally {
            buffer.release();
        }
    }

    @Test
    void oversizedEntryCountIsRejected() {
        RegistryFriendlyByteBuf buffer = newBuffer();
        try {
            buffer.writeVarInt(NetworkBundle.MAX_ENTRIES + 1);
            assertThrows(IllegalArgumentException.class, () -> NetworkBundle.CODEC.decode(buffer));
        } finally {
            buffer.release();
        }
    }

    @Test
    void partitionSplitsBundlesAtPacketLimit() {
        byte[] first = new byte[NetworkBundle.MAX_ENTRY_SIZE];
        byte[] second = new byte[NetworkBundle.MAX_ENTRY_SIZE];

        List<NetworkBundle> bundles = NetworkBundle.partition(List.of(
                new NetworkBundle.Entry(CHANNEL, "first", first),
                new NetworkBundle.Entry(CHANNEL, "second", second)
        ));

        assertEquals(2, bundles.size());
        assertEquals(1, bundles.get(0).entries().size());
        assertEquals(1, bundles.get(1).entries().size());
    }

    @Test
    void emptyPartitionProducesNoPackets() {
        assertEquals(List.of(), NetworkBundle.partition(List.of()));
    }

    private static RegistryFriendlyByteBuf newBuffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
    }
}

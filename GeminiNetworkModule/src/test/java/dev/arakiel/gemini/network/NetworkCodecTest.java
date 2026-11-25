package dev.arakiel.gemini.network;

import dev.arakiel.gemini.network.internal.NetworkHandshake;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NetworkCodecTest {
    @Test
    void recordCodecRoundTripsSupportedComponents() {
        StreamCodec<RegistryFriendlyByteBuf, TestMessage> codec = RecordCodecs.create(TestMessage.class);
        TestMessage message = new TestMessage(
                42,
                "hello",
                ResourceLocation.fromNamespaceAndPath("gemini_test", "message"),
                Optional.of("label"),
                List.of(1, 2, 3),
                new Vec3(1.5D, 2.5D, 3.5D),
                new BlockPos(4, 5, 6)
        );

        RegistryFriendlyByteBuf buffer = newBuffer();
        try {
            codec.encode(buffer, message);
            assertEquals(message, codec.decode(buffer));
        } finally {
            buffer.release();
        }
    }

    @Test
    void recordCodecRoundTripsPrimitiveCollectionsAndNestedRecords() {
        StreamCodec<RegistryFriendlyByteBuf, ComplexMessage> codec = RecordCodecs.create(ComplexMessage.class);
        ComplexMessage message = new ComplexMessage(
                (byte) -7,
                (short) 1200,
                '界',
                123456789L,
                1.25F,
                2.5D,
                new byte[]{1, 2, 3, 4},
                TestKind.SECOND,
                new LinkedHashSet<>(List.of("alpha", "beta")),
                new LinkedHashMap<>(Map.of("one", 1, "two", 2)),
                List.of(Optional.of("present"), Optional.empty()),
                new NestedMessage(
                        ResourceLocation.fromNamespaceAndPath("gemini_test", "nested"),
                        List.of(new NestedMessage(ResourceLocation.fromNamespaceAndPath("gemini_test", "child"), List.of()))
                )
        );

        RegistryFriendlyByteBuf buffer = newBuffer();
        try {
            codec.encode(buffer, message);
            ComplexMessage decoded = codec.decode(buffer);
            assertEquals(message.value(), decoded.value());
            assertEquals(message.shortValue(), decoded.shortValue());
            assertEquals(message.character(), decoded.character());
            assertEquals(message.longValue(), decoded.longValue());
            assertEquals(message.floatValue(), decoded.floatValue());
            assertEquals(message.doubleValue(), decoded.doubleValue());
            assertArrayEquals(message.payload(), decoded.payload());
            assertEquals(message.kind(), decoded.kind());
            assertEquals(message.tags(), decoded.tags());
            assertEquals(message.weights(), decoded.weights());
            assertEquals(message.notes(), decoded.notes());
            assertEquals(message.nested(), decoded.nested());
        } finally {
            buffer.release();
        }
    }

    @Test
    void unsupportedRecordComponentFailsFast() {
        assertThrows(IllegalArgumentException.class, () -> RecordCodecs.create(UnsupportedMessage.class));
    }

    @Test
    void handshakePayloadRoundTrips() {
        NetworkHandshake.HelloPayload hello = new NetworkHandshake.HelloPayload(
                new NetworkHandshake.ClientHello(
                        NetworkHandshake.PROTOCOL_VERSION,
                        List.of(ResourceLocation.fromNamespaceAndPath("gemini_test", "alpha")),
                        List.of(ResourceLocation.fromNamespaceAndPath("gemini_test", "alpha"))
                )
        );
        RegistryFriendlyByteBuf buffer = newBuffer();
        try {
            NetworkHandshake.HelloPayload.CODEC.encode(buffer, hello);
            assertEquals(hello, NetworkHandshake.HelloPayload.CODEC.decode(buffer));
        } finally {
            buffer.release();
        }
    }

    @Test
    void oversizedCollectionIsRejected() {
        RegistryFriendlyByteBuf buffer = newBuffer();
        try {
            buffer.writeVarInt(NetworkCodecs.MAX_COLLECTION_SIZE + 1);
            assertThrows(IllegalArgumentException.class,
                    () -> NetworkCodecs.list(NetworkCodecs.VAR_INT).decode(buffer));
        } finally {
            buffer.release();
        }
    }

    @Test
    void byteArrayLengthIsCheckedOnBothSides() {
        StreamCodec<RegistryFriendlyByteBuf, byte[]> codec = NetworkCodecs.byteArray(4);
        RegistryFriendlyByteBuf buffer = newBuffer();
        try {
            assertThrows(IllegalArgumentException.class, () -> codec.encode(buffer, new byte[5]));
            buffer.writeVarInt(5);
            assertThrows(IllegalArgumentException.class, () -> codec.decode(buffer));
        } finally {
            buffer.release();
        }
    }

    @Test
    void unknownEnumNameIsRejected() {
        RegistryFriendlyByteBuf buffer = newBuffer();
        try {
            buffer.writeUtf("MISSING");
            assertThrows(IllegalArgumentException.class,
                    () -> NetworkCodecs.enumCodec(TestKind.class).decode(buffer));
        } finally {
            buffer.release();
        }
    }

    private static RegistryFriendlyByteBuf newBuffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
    }

    record TestMessage(
            int count,
            String name,
            ResourceLocation id,
            Optional<String> label,
            List<Integer> values,
            Vec3 position,
            BlockPos blockPos
    ) {
    }

    record ComplexMessage(
            byte value,
            short shortValue,
            char character,
            long longValue,
            float floatValue,
            double doubleValue,
            byte[] payload,
            TestKind kind,
            Set<String> tags,
            Map<String, Integer> weights,
            List<Optional<String>> notes,
            NestedMessage nested
    ) {
    }

    record NestedMessage(ResourceLocation id, List<NestedMessage> children) {
    }

    enum TestKind {
        FIRST,
        SECOND
    }

    record UnsupportedMessage(Object value) {
    }
}

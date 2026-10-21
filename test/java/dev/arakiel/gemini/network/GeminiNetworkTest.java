package dev.arakiel.gemini.network;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiNetworkTest {
    @Test
    void topLevelFacadeExposesTheSharedChannelRegistry() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("gemini_test", "facade");
        NetworkChannel channel = GeminiNetwork.channel(id);

        assertSame(channel, GeminiNetwork.getChannel(id));
        assertTrue(GeminiNetwork.channels().contains(channel));
    }
}

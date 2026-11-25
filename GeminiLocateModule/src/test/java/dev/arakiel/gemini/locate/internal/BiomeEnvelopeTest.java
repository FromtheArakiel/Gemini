package dev.arakiel.gemini.locate.internal;

import net.minecraft.world.level.biome.Climate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BiomeEnvelopeTest {
    @Test
    void narrowEnvelopeRejectsClimateOutsideItsBounds() {
        BiomeEnvelope envelope = new BiomeEnvelope();
        envelope.add(Climate.parameters(-0.25F, -0.1F, 0.2F, 0.3F, 0.0F, 0.4F, 0.0F));

        assertFalse(envelope.empty());
        assertTrue(envelope.matches(
                Climate.quantizeCoord(-0.25F),
                Climate.quantizeCoord(-0.1F),
                Climate.quantizeCoord(0.2F),
                Climate.quantizeCoord(0.3F),
                Climate.quantizeCoord(0.4F)
        ));
        assertFalse(envelope.matches(
                Climate.quantizeCoord(0.8F),
                Climate.quantizeCoord(-0.1F),
                Climate.quantizeCoord(0.2F),
                Climate.quantizeCoord(0.3F),
                Climate.quantizeCoord(0.4F)
        ));
    }

    @Test
    void emptyEnvelopeRejectsEverySample() {
        BiomeEnvelope envelope = new BiomeEnvelope();

        assertTrue(envelope.empty());
        assertFalse(envelope.matches(0, 0, 0, 0, 0));
    }
}

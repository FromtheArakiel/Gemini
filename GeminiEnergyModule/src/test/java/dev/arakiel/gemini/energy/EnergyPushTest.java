package dev.arakiel.gemini.energy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EnergyPushTest {
    @Test
    void sourceSequentiallyFillsEndpointsLikeTeamRebornMove() {
        SimpleEnergyStorage source = new SimpleEnergyStorage(100, 0, 100, 100);
        SimpleEnergyStorage first = new SimpleEnergyStorage(40, 40, 0);
        SimpleEnergyStorage second = new SimpleEnergyStorage(100, 100, 0);

        EnergyPushResult result = GeminiEnergy.push(source, List.of(first, second), 100, false);

        assertEquals(100, result.available());
        assertEquals(100, result.delivered());
        assertEquals(0, source.amount());
        assertEquals(40, first.amount());
        assertEquals(60, second.amount());
    }

    @Test
    void simulateReportsDistributionWithoutMutation() {
        SimpleEnergyStorage source = new SimpleEnergyStorage(100, 0, 100, 100);
        SimpleEnergyStorage endpoint = new SimpleEnergyStorage(40, 40, 0);

        EnergyPushResult result = GeminiEnergy.push(source, List.of(endpoint), 100, true);

        assertEquals(40, result.delivered());
        assertEquals(100, source.amount());
        assertEquals(0, endpoint.amount());
    }

    @Test
    void capacityLimitBecomesRejectedEnergy() {
        SimpleEnergyStorage source = new SimpleEnergyStorage(100, 0, 100, 100);
        SimpleEnergyStorage endpoint = new SimpleEnergyStorage(20, 20, 0);

        EnergyPushResult result = GeminiEnergy.push(source, List.of(endpoint), 100, false);

        assertEquals(20, result.delivered());
        assertEquals(80, result.rejected());
        assertEquals(80, source.amount());
    }

    @Test
    void consumerThatCannotReceiveIsIgnored() {
        SimpleEnergyStorage source = new SimpleEnergyStorage(100, 0, 100, 100);
        SimpleEnergyStorage consumer = new SimpleEnergyStorage(100, 0, 100);

        EnergyPushResult result = GeminiEnergy.push(source, List.of(consumer), 100, false);

        assertEquals(0, result.delivered());
        assertFalse(result.movedEnergy());
    }
}

package dev.arakiel.gemini.energy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleEnergyStorageTest {
    @Test
    void receiveAndExtractRespectRatesAndCapacity() {
        SimpleEnergyStorage storage = new SimpleEnergyStorage(100, 20, 15);

        assertEquals(20, storage.receive(50, false));
        assertEquals(20, storage.receive(20, false));
        assertEquals(20, storage.receive(40, true));
        assertEquals(40, storage.amount());
        assertEquals(15, storage.extract(30, false));
        assertEquals(25, storage.amount());
    }

    @Test
    void simulateDoesNotMutateStorage() {
        SimpleEnergyStorage storage = new SimpleEnergyStorage(100, 100, 100, 30);

        assertEquals(70, storage.receive(70, true));
        assertEquals(30, storage.extract(100, true));
        assertEquals(30, storage.amount());
    }

    @Test
    void receiveAndExtractFlagsFollowConfiguredRates() {
        SimpleEnergyStorage receiver = new SimpleEnergyStorage(100, 10, 0);
        SimpleEnergyStorage extractor = new SimpleEnergyStorage(100, 0, 10);

        assertTrue(receiver.canReceive());
        assertFalse(receiver.canExtract());
        assertFalse(extractor.canReceive());
        assertTrue(extractor.canExtract());
    }

    @Test
    void snapshotCanRestoreAmount() {
        SimpleEnergyStorage storage = new SimpleEnergyStorage(100, 100, 100, 25);
        long snapshot = storage.snapshotAmount();

        storage.receive(40, false);
        assertEquals(65, storage.amount());
        storage.restoreAmount(snapshot);

        assertEquals(25, storage.amount());
    }
}

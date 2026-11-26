package dev.arakiel.gemini.capability;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnergyStorageViewTest {
    @Test
    void defaultContractSupportsBidirectionalEnergy() {
        EnergyStorageView storage = new TestEnergyStorage(100, 25);

        assertTrue(storage.canReceive());
        assertTrue(storage.canExtract());
        assertEquals(75, storage.remainingCapacity());
    }

    @Test
    void storageCanExposeReceiveOnlyConsumerShape() {
        EnergyStorageView storage = new TestEnergyStorage(100, 0) {
            @Override
            public boolean canExtract() {
                return false;
            }
        };

        assertFalse(storage.canExtract());
        assertTrue(storage.canReceive());
    }

    private static class TestEnergyStorage implements EnergyStorageView {
        private final long capacity;
        private long amount;

        private TestEnergyStorage(long capacity, long amount) {
            this.capacity = capacity;
            this.amount = amount;
        }

        @Override
        public long capacity() {
            return this.capacity;
        }

        @Override
        public long amount() {
            return this.amount;
        }

        @Override
        public long receive(long amount, boolean simulate) {
            long accepted = Math.min(amount, this.remainingCapacity());
            if (!simulate) {
                this.amount += accepted;
            }
            return accepted;
        }

        @Override
        public long extract(long amount, boolean simulate) {
            long extracted = Math.min(amount, this.amount);
            if (!simulate) {
                this.amount -= extracted;
            }
            return extracted;
        }
    }
}

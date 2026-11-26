package dev.arakiel.gemini.capability;

public interface EnergyStorageView {
    long capacity();

    long amount();

    long receive(long amount, boolean simulate);

    long extract(long amount, boolean simulate);

    default boolean canReceive() {
        return true;
    }

    default boolean canExtract() {
        return true;
    }

    default long remainingCapacity() {
        return Math.max(0L, capacity() - amount());
    }
}

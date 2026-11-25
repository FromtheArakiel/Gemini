package dev.arakiel.gemini.capability;

public interface EnergyStorageView {
    long capacity();

    long amount();

    long receive(long amount, boolean simulate);

    long extract(long amount, boolean simulate);
}

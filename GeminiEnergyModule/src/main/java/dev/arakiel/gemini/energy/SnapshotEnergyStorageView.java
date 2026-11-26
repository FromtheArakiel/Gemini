package dev.arakiel.gemini.energy;

import dev.arakiel.gemini.capability.EnergyStorageView;

public interface SnapshotEnergyStorageView extends EnergyStorageView {
    long snapshotAmount();

    void restoreAmount(long amount);
}

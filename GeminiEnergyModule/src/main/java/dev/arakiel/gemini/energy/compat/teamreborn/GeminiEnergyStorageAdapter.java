package dev.arakiel.gemini.energy.compat.teamreborn;

import dev.arakiel.gemini.energy.SnapshotEnergyStorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;

public final class GeminiEnergyStorageAdapter extends SnapshotParticipant<Long> implements EnergyStorage {
    private final SnapshotEnergyStorageView storage;

    public GeminiEnergyStorageAdapter(SnapshotEnergyStorageView storage) {
        this.storage = Objects.requireNonNull(storage, "storage");
    }

    @Override
    public boolean supportsInsertion() {
        return this.storage.canReceive();
    }

    @Override
    public long insert(long amount, TransactionContext transaction) {
        if (amount <= 0 || !this.supportsInsertion()) {
            return 0;
        }
        this.updateSnapshots(transaction);
        return this.storage.receive(amount, false);
    }

    @Override
    public boolean supportsExtraction() {
        return this.storage.canExtract();
    }

    @Override
    public long extract(long amount, TransactionContext transaction) {
        if (amount <= 0 || !this.supportsExtraction()) {
            return 0;
        }
        this.updateSnapshots(transaction);
        return this.storage.extract(amount, false);
    }

    @Override
    public long getAmount() {
        return this.storage.amount();
    }

    @Override
    public long getCapacity() {
        return this.storage.capacity();
    }

    @Override
    protected Long createSnapshot() {
        return this.storage.snapshotAmount();
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.storage.restoreAmount(snapshot);
    }
}

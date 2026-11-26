package dev.arakiel.gemini.energy.compat.teamreborn;

import dev.arakiel.gemini.capability.EnergyStorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;

public final class TeamRebornEnergyView implements EnergyStorageView {
    private final EnergyStorage storage;

    public TeamRebornEnergyView(EnergyStorage storage) {
        this.storage = Objects.requireNonNull(storage, "storage");
    }

    @Override
    public long capacity() {
        return this.storage.getCapacity();
    }

    @Override
    public long amount() {
        return this.storage.getAmount();
    }

    @Override
    public long receive(long amount, boolean simulate) {
        if (amount <= 0 || !this.canReceive()) {
            return 0;
        }
        try (Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
            long accepted = this.storage.insert(amount, transaction);
            if (!simulate) {
                transaction.commit();
            }
            return accepted;
        }
    }

    @Override
    public long extract(long amount, boolean simulate) {
        if (amount <= 0 || !this.canExtract()) {
            return 0;
        }
        try (Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
            long extracted = this.storage.extract(amount, transaction);
            if (!simulate) {
                transaction.commit();
            }
            return extracted;
        }
    }

    @Override
    public boolean canReceive() {
        return this.storage.supportsInsertion();
    }

    @Override
    public boolean canExtract() {
        return this.storage.supportsExtraction();
    }
}

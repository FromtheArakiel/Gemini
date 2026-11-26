package dev.arakiel.gemini.energy;

public final class SimpleEnergyStorage implements SnapshotEnergyStorageView {
    private final long capacity;
    private final long maxReceive;
    private final long maxExtract;
    private long amount;

    public SimpleEnergyStorage(long capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public SimpleEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public SimpleEnergyStorage(long capacity, long maxReceive, long maxExtract, long initialAmount) {
        if (capacity < 0 || maxReceive < 0 || maxExtract < 0 || initialAmount < 0) {
            throw new IllegalArgumentException("Energy values cannot be negative");
        }
        this.capacity = capacity;
        this.maxReceive = Math.min(maxReceive, capacity);
        this.maxExtract = Math.min(maxExtract, capacity);
        this.amount = Math.min(initialAmount, capacity);
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
        if (amount <= 0 || !this.canReceive()) {
            return 0;
        }
        long accepted = Math.min(Math.min(amount, this.maxReceive), this.remainingCapacity());
        if (!simulate) {
            this.amount += accepted;
        }
        return accepted;
    }

    @Override
    public long extract(long amount, boolean simulate) {
        if (amount <= 0 || !this.canExtract()) {
            return 0;
        }
        long extracted = Math.min(Math.min(amount, this.maxExtract), this.amount);
        if (!simulate) {
            this.amount -= extracted;
        }
        return extracted;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public long snapshotAmount() {
        return this.amount;
    }

    @Override
    public void restoreAmount(long amount) {
        this.amount = Math.max(0, Math.min(amount, this.capacity));
    }
}

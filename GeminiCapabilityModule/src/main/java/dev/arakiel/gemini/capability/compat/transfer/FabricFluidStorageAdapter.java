package dev.arakiel.gemini.capability.compat.transfer;

import dev.arakiel.gemini.capability.FluidStorageView;
import dev.arakiel.gemini.capability.GeminiFluidStack;
import dev.arakiel.gemini.capability.internal.TransactionSnapshots;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class FabricFluidStorageAdapter implements Storage<FluidVariant> {
    private final FluidStorageView storage;
    private final List<FluidTankView> tanks;
    private long version;

    public FabricFluidStorageAdapter(FluidStorageView storage) {
        this.storage = Objects.requireNonNull(storage, "storage");
        this.tanks = new ArrayList<>(storage.tanks());
        for (int tank = 0; tank < storage.tanks(); tank++) {
            this.tanks.add(new FluidTankView(this, tank));
        }
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (maxAmount == 0) {
            return 0;
        }
        TransactionSnapshots.fluidStorage(this.storage, transaction);
        long inserted = 0;
        for (int tank = 0; tank < this.storage.tanks() && inserted < maxAmount; tank++) {
            GeminiFluidStack current = this.storage.getTank(tank);
            if (!current.isEmpty() && !sameVariant(current, resource)) {
                continue;
            }
            long free = Math.max(0, this.storage.capacity(tank) - current.amount());
            long requested = Math.min(maxAmount - inserted, free);
            if (requested <= 0) {
                continue;
            }
            GeminiFluidStack offered = stack(resource, requested);
            inserted += this.storage.fill(tank, offered, requested, false);
        }
        if (inserted > 0) {
            this.version++;
        }
        return inserted;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (maxAmount == 0) {
            return 0;
        }
        TransactionSnapshots.fluidStorage(this.storage, transaction);
        long extracted = 0;
        for (int tank = 0; tank < this.storage.tanks() && extracted < maxAmount; tank++) {
            GeminiFluidStack current = this.storage.getTank(tank);
            if (current.isEmpty() || !sameVariant(current, resource)) {
                continue;
            }
            long requested = Math.min(maxAmount - extracted, current.amount());
            extracted += this.storage.drain(tank, requested, false).amount();
        }
        if (extracted > 0) {
            this.version++;
        }
        return extracted;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return List.<StorageView<FluidVariant>>copyOf(this.tanks).iterator();
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    FluidStorageView storage() {
        return this.storage;
    }

    void markChanged() {
        this.version++;
    }

    private static boolean sameVariant(GeminiFluidStack stack, FluidVariant variant) {
        return stack.fluid().value() == variant.getFluid()
                && Objects.equals(stack.components(), variant.getComponents());
    }

    private static GeminiFluidStack stack(FluidVariant variant, long amount) {
        return new GeminiFluidStack(variant.getRegistryEntry(), amount, variant.getComponents());
    }

    private static final class FluidTankView implements StorageView<FluidVariant> {
        private final FabricFluidStorageAdapter parent;
        private final int tank;

        private FluidTankView(FabricFluidStorageAdapter parent, int tank) {
            this.parent = parent;
            this.tank = tank;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            GeminiFluidStack current = this.parent.storage().getTank(this.tank);
            if (current.isEmpty() || !sameVariant(current, resource)) {
                return 0;
            }
            TransactionSnapshots.fluidStorage(this.parent.storage(), transaction);
            long extracted = this.parent.storage().drain(
                    this.tank,
                    Math.min(maxAmount, current.amount()),
                    false
            ).amount();
            if (extracted > 0) {
                this.parent.markChanged();
            }
            return extracted;
        }

        @Override
        public boolean isResourceBlank() {
            return this.getResource().isBlank();
        }

        @Override
        public FluidVariant getResource() {
            GeminiFluidStack stack = this.parent.storage().getTank(this.tank);
            return stack.isEmpty()
                    ? FluidVariant.blank()
                    : FluidVariant.of(stack.fluid().value(), stack.components());
        }

        @Override
        public long getAmount() {
            return this.parent.storage().getTank(this.tank).amount();
        }

        @Override
        public long getCapacity() {
            return this.parent.storage().capacity(this.tank);
        }
    }
}

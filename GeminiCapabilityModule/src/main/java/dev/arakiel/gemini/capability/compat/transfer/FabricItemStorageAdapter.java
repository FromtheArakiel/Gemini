package dev.arakiel.gemini.capability.compat.transfer;

import dev.arakiel.gemini.capability.ItemStorageView;
import dev.arakiel.gemini.capability.internal.TransactionSnapshots;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.Objects;

public final class FabricItemStorageAdapter implements SlottedStorage<ItemVariant> {
    private final ItemStorageView storage;
    private final int slots;
    private final FabricItemSlotAdapter[] slotAdapters;
    private long version;

    public FabricItemStorageAdapter(ItemStorageView storage) {
        this.storage = Objects.requireNonNull(storage, "storage");
        this.slots = storage.slots();
        this.slotAdapters = new FabricItemSlotAdapter[this.slots];
        for (int slot = 0; slot < this.slots; slot++) {
            this.slotAdapters[slot] = new FabricItemSlotAdapter(this, slot);
        }
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (maxAmount == 0) {
            return 0;
        }
        TransactionSnapshots.itemStorage(this.storage, transaction);
        long inserted = 0;
        for (int slot = 0; slot < this.slots && inserted < maxAmount; slot++) {
            int accepted = (int) Math.min(Integer.MAX_VALUE, maxAmount - inserted);
            ItemStack remaining = this.storage.insert(slot, resource.toStack(accepted), false);
            int moved = accepted - remaining.getCount();
            inserted += moved;
        }
        if (inserted > 0) {
            this.version++;
        }
        return inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (maxAmount == 0) {
            return 0;
        }
        TransactionSnapshots.itemStorage(this.storage, transaction);
        long extracted = 0;
        for (int slot = 0; slot < this.slots && extracted < maxAmount; slot++) {
            ItemStack current = this.storage.getSlot(slot);
            if (current.isEmpty() || !resource.matches(current)) {
                continue;
            }
            int requested = (int) Math.min(Math.min(Integer.MAX_VALUE, maxAmount - extracted), current.getCount());
            extracted += this.storage.extract(slot, requested, false).getCount();
        }
        if (extracted > 0) {
            this.version++;
        }
        return extracted;
    }

    @Override
    public int getSlotCount() {
        return this.slots;
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        if (slot < 0 || slot >= this.slots) {
            throw new IndexOutOfBoundsException("Slot " + slot + " is outside [0, " + this.slots + ")");
        }
        return this.slotAdapters[slot];
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        return java.util.Arrays.<StorageView<ItemVariant>>asList(this.slotAdapters).iterator();
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    void markChanged() {
        this.version++;
    }

    ItemStorageView storage() {
        return this.storage;
    }
}

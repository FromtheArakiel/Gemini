package dev.arakiel.gemini.capability.compat.transfer;

import dev.arakiel.gemini.capability.internal.TransactionSnapshots;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

final class FabricItemSlotAdapter implements SingleSlotStorage<ItemVariant> {
    private final FabricItemStorageAdapter parent;
    private final int slot;

    FabricItemSlotAdapter(FabricItemStorageAdapter parent, int slot) {
        this.parent = parent;
        this.slot = slot;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (maxAmount == 0) {
            return 0;
        }
        TransactionSnapshots.itemStorage(this.parent.storage(), transaction);
        int requested = (int) Math.min(Integer.MAX_VALUE, maxAmount);
        ItemStack remaining = this.parent.storage().insert(this.slot, resource.toStack(requested), false);
        int inserted = requested - remaining.getCount();
        if (inserted > 0) {
            this.parent.markChanged();
        }
        return inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        ItemStack current = this.parent.storage().getSlot(this.slot);
        if (current.isEmpty() || !resource.matches(current)) {
            return 0;
        }
        int requested = (int) Math.min(Math.min(Integer.MAX_VALUE, maxAmount), current.getCount());
        ItemStack extracted = this.parent.storage().extract(this.slot, requested, false);
        if (!extracted.isEmpty()) {
            this.parent.markChanged();
        }
        return extracted.getCount();
    }

    @Override
    public boolean isResourceBlank() {
        return this.getResource().isBlank();
    }

    @Override
    public ItemVariant getResource() {
        ItemStack stack = this.parent.storage().getSlot(this.slot);
        return stack.isEmpty() ? ItemVariant.blank() : ItemVariant.of(stack);
    }

    @Override
    public long getAmount() {
        ItemStack stack = this.parent.storage().getSlot(this.slot);
        return stack.isEmpty() ? 0 : stack.getCount();
    }

    @Override
    public long getCapacity() {
        return this.parent.storage().slotLimit(this.slot);
    }
}

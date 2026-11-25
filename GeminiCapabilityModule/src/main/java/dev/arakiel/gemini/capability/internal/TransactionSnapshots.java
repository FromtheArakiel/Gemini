package dev.arakiel.gemini.capability.internal;

import dev.arakiel.gemini.capability.GeminiFluidStack;
import dev.arakiel.gemini.capability.FluidStorageView;
import dev.arakiel.gemini.capability.ItemStorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TransactionSnapshots {
    private TransactionSnapshots() {
    }

    public static void itemStorage(ItemStorageView storage, TransactionContext transaction) {
        List<ItemStack> snapshot = new ArrayList<>(storage.slots());
        for (int slot = 0; slot < storage.slots(); slot++) {
            snapshot.add(storage.getSlot(slot).copy());
        }
        AtomicBoolean restored = new AtomicBoolean();
        transaction.addCloseCallback((closed, result) ->
                restoreOnce(restored, result, () -> restoreItems(storage, snapshot)));
        transaction.addOuterCloseCallback(result ->
                restoreOnce(restored, result, () -> restoreItems(storage, snapshot)));
    }

    public static void fluidStorage(FluidStorageView storage, TransactionContext transaction) {
        List<GeminiFluidStack> snapshot = new ArrayList<>(storage.tanks());
        for (int tank = 0; tank < storage.tanks(); tank++) {
            snapshot.add(storage.getTank(tank).copy());
        }
        AtomicBoolean restored = new AtomicBoolean();
        transaction.addCloseCallback((closed, result) ->
                restoreOnce(restored, result, () -> restoreFluids(storage, snapshot)));
        transaction.addOuterCloseCallback(result ->
                restoreOnce(restored, result, () -> restoreFluids(storage, snapshot)));
    }

    private static void restoreItems(ItemStorageView storage, List<ItemStack> snapshot) {
        for (int slot = 0; slot < Math.min(storage.slots(), snapshot.size()); slot++) {
            ItemStack current = storage.getSlot(slot);
            if (current == null) {
                continue;
            }
            if (!current.isEmpty()) {
                storage.extract(slot, current.getCount(), false);
            }
            ItemStack expected = snapshot.get(slot);
            if (!expected.isEmpty()) {
                storage.insert(slot, expected.copy(), false);
            }
        }
    }

    private static void restoreFluids(FluidStorageView storage, List<GeminiFluidStack> snapshot) {
        for (int tank = 0; tank < Math.min(storage.tanks(), snapshot.size()); tank++) {
            GeminiFluidStack current = storage.getTank(tank);
            if (!current.isEmpty()) {
                storage.drain(tank, current.amount(), false);
            }
            GeminiFluidStack expected = snapshot.get(tank);
            if (!expected.isEmpty()) {
                storage.fill(tank, expected.copy(), expected.amount(), false);
            }
        }
    }

    private static void restoreOnce(
            AtomicBoolean restored,
            TransactionContext.Result result,
            Runnable rollback
    ) {
        if (result.wasAborted() && restored.compareAndSet(false, true)) {
            rollback.run();
        }
    }
}

package dev.arakiel.gemini.capability.compat.transfer;

import dev.arakiel.gemini.capability.FluidStorageView;
import dev.arakiel.gemini.capability.GeminiFluidStack;
import dev.arakiel.gemini.capability.ItemStorageView;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FabricStorageAdapterTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void itemInsertAndSimulationPreserveState() {
        TestItemStorage storage = new TestItemStorage();
        FabricItemStorageAdapter adapter = new FabricItemStorageAdapter(storage);
        ItemVariant stone = new TestItemVariant(Items.STONE, DataComponentPatch.EMPTY);

        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(4, adapter.insert(stone, 4, transaction));
            transaction.abort();
        }
        assertTrue(storage.stacks[0].isEmpty());

        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(4, adapter.insert(stone, 4, transaction));
            transaction.commit();
        }
        assertEquals(Items.STONE, storage.stacks[0].getItem());
        assertEquals(4, storage.stacks[0].getCount());

        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(3, adapter.extract(stone, 3, transaction));
            transaction.commit();
        }
        assertEquals(1, storage.stacks[0].getCount());
    }

    @Test
    void nestedCommitStillRollsBackWhenOuterTransactionAborts() {
        TestItemStorage storage = new TestItemStorage();
        FabricItemStorageAdapter adapter = new FabricItemStorageAdapter(storage);
        ItemVariant stone = new TestItemVariant(Items.STONE, DataComponentPatch.EMPTY);

        try (Transaction outer = Transaction.openOuter()) {
            try (Transaction nested = outer.openNested()) {
                assertEquals(2, adapter.insert(stone, 2, nested));
                nested.commit();
            }
            assertEquals(2, storage.stacks[0].getCount());
            outer.abort();
        }

        assertTrue(storage.stacks[0].isEmpty());
    }

    @Test
    void itemAdapterPreservesDataComponents() {
        TestItemStorage storage = new TestItemStorage();
        FabricItemStorageAdapter adapter = new FabricItemStorageAdapter(storage);
        DataComponentPatch patch = DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_NAME, Component.literal("Gemini Test"))
                .build();
        ItemVariant namedStone = new TestItemVariant(Items.STONE, patch);

        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(1, adapter.insert(namedStone, 1, transaction));
            transaction.commit();
        }

        assertEquals(patch, storage.stacks[0].getComponentsPatch());
    }

    @Test
    void fluidAdapterKeepsVariantAndReportsActualAmount() {
        TestFluidStorage storage = new TestFluidStorage();
        FabricFluidStorageAdapter adapter = new FabricFluidStorageAdapter(storage);
        DataComponentPatch patch = DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_NAME, Component.literal("Gemini Fluid"))
                .build();
        FluidVariant water = new TestFluidVariant(
                Fluids.WATER,
                BuiltInRegistries.FLUID.wrapAsHolder(Fluids.WATER),
                patch
        );

        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(100, adapter.insert(water, 100, transaction));
            transaction.commit();
        }
        assertEquals(100, storage.current.amount());
        assertEquals(Fluids.WATER, storage.current.fluid().value());
        assertEquals(patch, storage.current.components());

        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(30, adapter.extract(water, 30, transaction));
            transaction.commit();
        }
        assertEquals(70, storage.current.amount());
    }

    private static final class TestItemStorage implements ItemStorageView {
        private final ItemStack[] stacks = {ItemStack.EMPTY, ItemStack.EMPTY};

        @Override
        public int slots() {
            return this.stacks.length;
        }

        @Override
        public ItemStack getSlot(int slot) {
            return this.stacks[slot];
        }

        @Override
        public int slotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return !stack.isEmpty();
        }

        @Override
        public ItemStack insert(int slot, ItemStack stack, boolean simulate) {
            ItemStack current = this.stacks[slot];
            if (!this.isValid(slot, stack)) {
                return stack;
            }
            if (!current.isEmpty() && !ItemStack.isSameItemSameComponents(current, stack)) {
                return stack;
            }
            int accepted = Math.min(stack.getCount(), this.slotLimit(slot) - current.getCount());
            ItemStack remainder = stack.copyWithCount(stack.getCount() - accepted);
            if (!simulate && accepted > 0) {
                if (current.isEmpty()) {
                    this.stacks[slot] = stack.copyWithCount(accepted);
                } else {
                    current.grow(accepted);
                }
            }
            return remainder;
        }

        @Override
        public ItemStack extract(int slot, int amount, boolean simulate) {
            ItemStack current = this.stacks[slot];
            int extracted = Math.min(amount, current.getCount());
            if (!simulate && extracted > 0) {
                ItemStack result = current.copyWithCount(extracted);
                current.shrink(extracted);
                return result;
            }
            return current.copyWithCount(extracted);
        }
    }

    private record TestItemVariant(Item item, DataComponentPatch components) implements ItemVariant {
        @Override
        public boolean isBlank() {
            return false;
        }

        @Override
        public Item getObject() {
            return this.item;
        }

        @Override
        public DataComponentPatch getComponents() {
            return this.components;
        }

        @Override
        public DataComponentMap getComponentMap() {
            return DataComponentMap.EMPTY;
        }

        @Override
        public ItemStack toStack(int count) {
            return new ItemStack(BuiltInRegistries.ITEM.wrapAsHolder(this.item), count, this.components);
        }

        @Override
        public ItemVariant withComponentChanges(DataComponentPatch changes) {
            return this;
        }
    }

    private record TestFluidVariant(
            Fluid fluid,
            Holder<Fluid> registryEntry,
            DataComponentPatch components
    ) implements FluidVariant {
        @Override
        public boolean isBlank() {
            return false;
        }

        @Override
        public Fluid getObject() {
            return this.fluid;
        }

        @Override
        public Holder<Fluid> getRegistryEntry() {
            return this.registryEntry;
        }

        @Override
        public DataComponentPatch getComponents() {
            return this.components;
        }

        @Override
        public DataComponentMap getComponentMap() {
            return DataComponentMap.EMPTY;
        }

        @Override
        public FluidVariant withComponentChanges(DataComponentPatch changes) {
            return this;
        }
    }

    private static final class TestFluidStorage implements FluidStorageView {
        private GeminiFluidStack current = GeminiFluidStack.empty();

        @Override
        public int tanks() {
            return 1;
        }

        @Override
        public GeminiFluidStack getTank(int tank) {
            return this.current;
        }

        @Override
        public long capacity(int tank) {
            return 100;
        }

        @Override
        public long fill(int tank, GeminiFluidStack resource, long amount, boolean simulate) {
            if (!this.current.isEmpty() && !this.current.fluid().equals(resource.fluid())) {
                return 0;
            }
            long accepted = Math.min(amount, this.capacity(tank) - this.current.amount());
            if (!simulate && accepted > 0) {
                this.current = resource.withAmount(this.current.amount() + accepted);
            }
            return accepted;
        }

        @Override
        public GeminiFluidStack drain(int tank, long amount, boolean simulate) {
            long drained = Math.min(amount, this.current.amount());
            GeminiFluidStack result = this.current.withAmount(drained);
            if (!simulate && drained > 0) {
                this.current = this.current.withAmount(this.current.amount() - drained);
            }
            return result;
        }
    }
}

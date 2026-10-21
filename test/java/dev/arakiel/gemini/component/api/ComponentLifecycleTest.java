package dev.arakiel.gemini.component.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentLifecycleTest {
    @Test
    void commonTickingRunsForBothSides() {
        TickingComponent component = new TickingComponent();

        component.serverTick();
        component.clientTick();
        component.clientTick();

        assertEquals(3, component.ticks);
    }

    private static final class TickingComponent implements ComponentLifecycle.CommonTicking {
        private int ticks;

        @Override
        public void tick() {
            ticks++;
        }

        @Override
        public void readFromNbt(CompoundTag tag, HolderLookup.Provider registries) {
        }

        @Override
        public void writeToNbt(CompoundTag tag, HolderLookup.Provider registries) {
        }
    }
}

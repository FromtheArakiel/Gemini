package dev.arakiel.gemini.component.api;

import io.netty.buffer.Unpooled;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentSyncTest {
    @Test
    void autoSyncedComponentWritesAndAppliesNbt() {
        SyncedComponent source = new SyncedComponent();
        source.value = 37;
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        try {
            source.writeSyncPacket(buffer, null);
            SyncedComponent target = new SyncedComponent();
            target.applySyncPacket(buffer);
            assertEquals(37, target.value);
        } finally {
            buffer.release();
        }
    }

    @Test
    void c2sEmptyWriterProducesNoBytes() {
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        try {
            ComponentSync.C2SPacketWriter.EMPTY.writeC2SPacket(buffer);
            assertEquals(0, buffer.readableBytes());
            assertTrue(ComponentSync.PlayerPredicate.all().shouldSyncWith(null));
        } finally {
            buffer.release();
        }
    }

    private static final class SyncedComponent implements ComponentSync.AutoSyncedComponent {
        private int value;

        @Override
        public void readFromNbt(CompoundTag tag, HolderLookup.Provider registries) {
            value = tag.getInt("value");
        }

        @Override
        public void writeToNbt(CompoundTag tag, HolderLookup.Provider registries) {
            tag.putInt("value", value);
        }
    }
}

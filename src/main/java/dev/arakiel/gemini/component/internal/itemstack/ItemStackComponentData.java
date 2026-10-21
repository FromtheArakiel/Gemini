package dev.arakiel.gemini.component.internal.itemstack;

import com.mojang.serialization.Codec;
import java.util.Objects;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class ItemStackComponentData {
    static final Codec<ItemStackComponentData> CODEC = CompoundTag.CODEC.xmap(
            tag -> new ItemStackComponentData(tag, ItemStackComponentDataContext.current()),
            data -> data.snapshot().copy()
    );

    static final StreamCodec<RegistryFriendlyByteBuf, ItemStackComponentData> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, data) -> buffer.writeNbt(data.snapshot()),
                    buffer -> {
                        CompoundTag tag = buffer.readNbt();
                        return new ItemStackComponentData(
                                tag == null ? new CompoundTag() : tag,
                                buffer.registryAccess()
                        );
                    }
            );

    private final CompoundTag snapshot;
    private final Provider provider;

    ItemStackComponentData(CompoundTag snapshot, Provider provider) {
        this.snapshot = snapshot.copy();
        this.provider = provider == null ? net.minecraft.core.RegistryAccess.EMPTY : provider;
    }

    CompoundTag snapshot() {
        return this.snapshot.copy();
    }

    Provider provider() {
        return this.provider;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ItemStackComponentData other
                && this.snapshot.equals(other.snapshot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.snapshot);
    }

    @Override
    public String toString() {
        return "ItemStackComponentData" + this.snapshot;
    }
}

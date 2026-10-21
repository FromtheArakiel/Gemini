package dev.arakiel.gemini.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public abstract class ConditionalToken {
    public boolean tick(Player player) {
        return false;
    }

    public boolean retainOnDeath(Player player) {
        return false;
    }

    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registries) {
    }

    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registries) {
    }
}

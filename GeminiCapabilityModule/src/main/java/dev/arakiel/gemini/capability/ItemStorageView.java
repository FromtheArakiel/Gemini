package dev.arakiel.gemini.capability;

import net.minecraft.world.item.ItemStack;

public interface ItemStorageView {
    int slots();

    ItemStack getSlot(int slot);

    int slotLimit(int slot);

    boolean isValid(int slot, ItemStack stack);

    ItemStack insert(int slot, ItemStack stack, boolean simulate);

    ItemStack extract(int slot, int amount, boolean simulate);
}

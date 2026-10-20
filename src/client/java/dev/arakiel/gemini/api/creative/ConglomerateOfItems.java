package dev.arakiel.gemini.api.creative;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ConglomerateOfItems {
    private final List<Object> entries = new ArrayList<>();
    private final List<ItemStack> stacks = new ArrayList<>();

    private ConglomerateOfItems() {
    }

    public static ConglomerateOfItems create() {
        return new ConglomerateOfItems();
    }

    public List<ItemStack> getStacks() {
        return stacks;
    }

    public void resolveStacks(RegistryAccess registryAccess) {
        stacks.clear();
        for (Object entry : entries) {
            if (entry instanceof ItemStack stack) {
                stacks.add(stack);
            } else if (entry instanceof ItemLike itemLike) {
                stacks.add(itemLike.asItem().getDefaultInstance());
            } else if (entry instanceof Supplier<?> supplier && supplier.get() instanceof ItemStack stack) {
                stacks.add(stack);
            } else if (entry instanceof List<?> list) {
                for (Object value : list) {
                    if (value instanceof ItemStack stack) {
                        stacks.add(stack);
                    }
                }
            } else if (entry instanceof RegistryDependentEntry registryEntry && registryAccess != null) {
                stacks.addAll(registryEntry.resolve(registryAccess));
            }
        }
    }

    public ConglomerateOfItems add(Item item) {
        entries.add(item);
        return this;
    }

    public ConglomerateOfItems add(ItemStack stack) {
        entries.add(stack);
        return this;
    }

    public ConglomerateOfItems add(ItemLike itemLike) {
        entries.add(itemLike);
        return this;
    }

    public ConglomerateOfItems add(Supplier<ItemStack> supplier) {
        entries.add(supplier);
        return this;
    }

    public ConglomerateOfItems add(List<ItemStack> itemStacks) {
        entries.add(itemStacks);
        return this;
    }

    public ConglomerateOfItems add(RegistryDependentEntry entry) {
        entries.add(entry);
        return this;
    }

    @FunctionalInterface
    public interface RegistryDependentEntry {
        List<ItemStack> resolve(RegistryAccess registryAccess);
    }
}

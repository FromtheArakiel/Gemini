package dev.arakiel.gemini.tweak;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface FastUseItem {
    default boolean isFastUse(ItemStack stack) {
        return true;
    }

    static boolean matches(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof FastUseItem fast && fast.isFastUse(stack);
    }
}

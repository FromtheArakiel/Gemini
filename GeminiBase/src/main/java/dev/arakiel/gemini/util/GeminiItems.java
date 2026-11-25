package dev.arakiel.gemini.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class GeminiItems {
    private GeminiItems() {
    }

    public static void give(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    public static void give(Player player, ItemLike item, int count) {
        give(player, new ItemStack(item, Math.max(0, count)));
    }

    public static boolean isHolding(Player player, ItemLike item) {
        return player.getMainHandItem().is(item.asItem()) || player.getOffhandItem().is(item.asItem());
    }
}

package dev.arakiel.gemini.event.internal;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class GeminiDropCapture {
    private static final ThreadLocal<List<ItemStack>> CAPTURE = new ThreadLocal<>();

    private GeminiDropCapture() {
    }

    public static void begin() {
        CAPTURE.set(new ArrayList<>());
    }

    public static List<ItemStack> finish() {
        List<ItemStack> drops = CAPTURE.get();
        CAPTURE.remove();
        return drops;
    }

    public static boolean capture(ItemStack stack) {
        List<ItemStack> drops = CAPTURE.get();
        if (drops == null) {
            return false;
        }
        drops.add(stack.copy());
        return true;
    }
}

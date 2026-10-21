package dev.arakiel.gemini.creative;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.creative.internal.GeminiCreativeTabsImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class GeminiCreativeTabs {
    private GeminiCreativeTabs() {
    }

    public static ResourceKey<CreativeModeTab> register(
            ResourceLocation id,
            Component title,
            Supplier<ItemStack> icon,
            Consumer<CreativeModeTab.Output> entries
    ) {
        return register(id, CreativeModeTab.Row.TOP, 0, title, icon, entries);
    }

    public static ResourceKey<CreativeModeTab> register(
            ResourceLocation id,
            CreativeModeTab.Row row,
            int column,
            Component title,
            Supplier<ItemStack> icon,
            Consumer<CreativeModeTab.Output> entries
    ) {
        return GeminiCreativeTabsImpl.register(id, row, column, title, icon, entries);
    }

    public static ResourceKey<CreativeModeTab> register(
            String path,
            Component title,
            Supplier<ItemStack> icon,
            Consumer<CreativeModeTab.Output> entries
    ) {
        return register(Gemini.id(path), title, icon, entries);
    }

    public static ResourceKey<CreativeModeTab> register(
            String path,
            CreativeModeTab.Row row,
            int column,
            Component title,
            Supplier<ItemStack> icon,
            Consumer<CreativeModeTab.Output> entries
    ) {
        return register(Gemini.id(path), row, column, title, icon, entries);
    }

    public static void add(ResourceKey<CreativeModeTab> tab, ItemLike... items) {
        GeminiCreativeTabsImpl.add(tab, items);
    }

    public static void addStacks(
            ResourceKey<CreativeModeTab> tab,
            Supplier<? extends Collection<ItemStack>> stacks
    ) {
        GeminiCreativeTabsImpl.addStacks(tab, stacks);
    }
}

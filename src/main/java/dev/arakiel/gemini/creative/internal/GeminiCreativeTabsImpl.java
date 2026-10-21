package dev.arakiel.gemini.creative.internal;

import dev.arakiel.gemini.Gemini;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class GeminiCreativeTabsImpl {
    private GeminiCreativeTabsImpl() {
    }

    public static ResourceKey<CreativeModeTab> register(
            ResourceLocation id,
            CreativeModeTab.Row row,
            int column,
            Component title,
            Supplier<ItemStack> icon,
            Consumer<CreativeModeTab.Output> entries
    ) {
        CreativeModeTab tab = CreativeModeTab.builder(row, column)
                .title(title)
                .icon(icon)
                .displayItems((parameters, output) -> entries.accept(output))
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, tab);
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, id);
    }

    public static void add(ResourceKey<CreativeModeTab> tab, ItemLike... items) {
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> {
            for (ItemLike item : items) {
                entries.accept(new ItemStack(item));
            }
        });
    }

    public static void addStacks(
            ResourceKey<CreativeModeTab> tab,
            Supplier<? extends Collection<ItemStack>> stacks
    ) {
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> {
            for (ItemStack stack : stacks.get()) {
                entries.accept(stack);
            }
        });
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
}

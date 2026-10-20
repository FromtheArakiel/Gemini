package dev.arakiel.gemini.api.creative;

import dev.arakiel.gemini.Gemini;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class FancyTabSections {
    private static final Map<ResourceLocation, List<Section<?>>> SECTIONS = new HashMap<>();

    private FancyTabSections() {
    }

    public static void addSection(ResourceLocation tab, Section<?> section) {
        SECTIONS.computeIfAbsent(tab, key -> new ArrayList<>()).add(section);
    }

    public static void addSection(ResourceKey<CreativeModeTab> tab, Section<?> section) {
        addSection(tab.location(), section);
    }

    public static List<Section<?>> getSections(ResourceLocation tab) {
        return SECTIONS.getOrDefault(tab, List.of());
    }

    public static List<Section<?>> getSections(CreativeModeTab tab) {
        return getSections(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab));
    }

    public static boolean hasSections(ResourceLocation tab) {
        List<Section<?>> sections = SECTIONS.get(tab);
        return sections != null && !sections.isEmpty();
    }

    public static Section<?> getSection(ResourceLocation id) {
        for (List<Section<?>> sections : SECTIONS.values()) {
            for (Section<?> section : sections) {
                if (section.id().equals(id)) {
                    return section;
                }
            }
        }
        return null;
    }

    public static Map<ResourceLocation, List<Section<?>>> getAllSections() {
        return Collections.unmodifiableMap(SECTIONS);
    }

    public static ResourceKey<CreativeModeTab> registerCreativeModeTab(ResourceLocation id, Supplier<ItemStack> icon) {
        return GeminiCreativeTabs.register(
                id,
                Component.translatable("itemGroup." + id.getNamespace() + "." + id.getPath()),
                icon,
                output -> {
                }
        );
    }

    public static ResourceKey<CreativeModeTab> registerCreativeModeTab(ResourceLocation id, ItemLike icon) {
        return registerCreativeModeTab(id, () -> new ItemStack(icon));
    }

    public static ResourceLocation id(String path) {
        return Gemini.id(path);
    }
}

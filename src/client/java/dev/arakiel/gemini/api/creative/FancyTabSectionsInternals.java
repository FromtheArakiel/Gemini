package dev.arakiel.gemini.api.creative;

import dev.arakiel.gemini.mixin.fancy.CreativeModeTabAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class FancyTabSectionsInternals {
    private static final Set<Section<?>> COLLAPSED = new HashSet<>();

    private FancyTabSectionsInternals() {
    }

    public static void applyItems(CreativeModeTab tab) {
        ResourceLocation tabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
        List<Section<?>> sections = FancyTabSections.getSections(tabId);
        if (sections.isEmpty()) {
            return;
        }

        RegistryAccess registryAccess = Minecraft.getInstance().level == null
                ? null
                : Minecraft.getInstance().level.registryAccess();
        refreshAllItems(registryAccess);

        List<ItemStack> stacks = new ArrayList<>();
        for (Section<?> section : sections) {
            for (int i = 0; i < 9; i++) {
                stacks.add(ItemStack.EMPTY);
            }
            if (isCollapsed(section)) {
                continue;
            }
            stacks.addAll(section.items().getStacks());
            int used = stacks.size() % 9;
            if (used != 0) {
                for (int i = 0; i < 9 - used; i++) {
                    stacks.add(ItemStack.EMPTY);
                }
            }
        }

        CreativeModeTabAccessor accessor = (CreativeModeTabAccessor) (Object) tab;
        accessor.gemini$setDisplayItems(stacks);
        accessor.gemini$setDisplayItemsSearchTab(
                stacks.stream().filter(stack -> !stack.isEmpty()).collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }

    public static void refreshAllItems(RegistryAccess registryAccess) {
        for (List<Section<?>> sections : FancyTabSections.getAllSections().values()) {
            for (Section<?> section : sections) {
                section.items().resolveStacks(registryAccess);
                if (registryAccess != null) {
                    section.onReload(registryAccess);
                }
            }
        }
    }

    public static int getRowForSection(Section<?> target) {
        for (List<Section<?>> sections : FancyTabSections.getAllSections().values()) {
            int row = 0;
            for (Section<?> section : sections) {
                if (section == target) {
                    int contentRows = isCollapsed(section)
                            ? 0
                            : (section.items().getStacks().size() - 1) / 9 + 1;
                    if (section instanceof dev.arakiel.gemini.api.creative.StickySection sticky
                            && sticky.isSticky()
                            && BannerRenderer.CURRENT_ROW >= row
                            && BannerRenderer.CURRENT_ROW <= row + contentRows) {
                        return BannerRenderer.CURRENT_ROW;
                    }
                    return row;
                }
                row++;
                if (!isCollapsed(section)) {
                    row += (section.items().getStacks().size() - 1) / 9 + 1;
                }
            }
        }
        return -1;
    }

    public static boolean isCollapsed(Section<?> section) {
        return COLLAPSED.contains(section);
    }

    public static void toggle(Section<?> section) {
        if (isCollapsed(section)) {
            expand(section);
        } else {
            collapse(section);
        }
    }

    public static void collapse(Section<?> section) {
        COLLAPSED.add(section);
    }

    public static void expand(Section<?> section) {
        COLLAPSED.remove(section);
    }

    public static boolean isBannerRow(ResourceLocation tab, int row) {
        for (Section<?> section : FancyTabSections.getSections(tab)) {
            if (getRowForSection(section) == row) {
                return true;
            }
        }
        return false;
    }
}

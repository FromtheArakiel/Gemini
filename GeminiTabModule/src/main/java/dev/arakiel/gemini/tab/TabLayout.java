package dev.arakiel.gemini.tab;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.resources.ResourceLocation;

public record TabLayout(
        List<TabDefinition> tabs,
        boolean inventoryVisible,
        Optional<ResourceLocation> selectedId
) {
    public TabLayout {
        tabs = List.copyOf(Objects.requireNonNull(tabs, "tabs"));
        selectedId = Objects.requireNonNull(selectedId, "selectedId");
    }

    public boolean isEmpty() {
        return !this.inventoryVisible;
    }

    public int totalSize() {
        return this.tabs.size() + (this.inventoryVisible ? 1 : 0);
    }
}

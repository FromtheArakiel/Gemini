package dev.arakiel.gemini.tab;

import dev.arakiel.gemini.tab.internal.TabOrdering;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class TabRegistry {
    private final Map<ResourceLocation, RegisteredTab> tabs = new LinkedHashMap<>();
    private List<TabDefinition> orderedTabs = List.of();
    private State state = State.COLLECTING;
    private long sequence;

    public synchronized TabRegistration register(TabDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        RegisteredTab existing = this.tabs.get(definition.id());
        if (existing != null) {
            TabRegistration.Status status = existing.definition().owner().equals(definition.owner())
                    ? TabRegistration.Status.ALREADY_REGISTERED
                    : TabRegistration.Status.CONFLICT;
            return new TabRegistration(existing.handle(), status);
        }

        long registrationSequence = this.sequence++;
        TabHandle handle = new TabHandle(definition.id(), definition.owner(), registrationSequence);
        if (this.state == State.SEALED) {
            return new TabRegistration(handle, TabRegistration.Status.TOO_LATE);
        }

        this.tabs.put(definition.id(), new RegisteredTab(handle, definition));
        return new TabRegistration(handle, TabRegistration.Status.REGISTERED);
    }

    public synchronized void seal() {
        if (this.state == State.SEALED) {
            return;
        }
        List<TabDefinition> definitions = this.tabs.values().stream()
                .map(RegisteredTab::definition)
                .toList();
        this.orderedTabs = TabOrdering.order(definitions);
        this.state = State.SEALED;
    }

    public synchronized TabLayout layout(TabContext context) {
        Objects.requireNonNull(context, "context");
        this.seal();
        List<TabDefinition> visible = new ArrayList<>();
        for (TabDefinition definition : this.orderedTabs) {
            if (definition.visibleWhen().test(context)) {
                visible.add(definition);
            }
        }
        boolean inventoryVisible = !visible.isEmpty();
        ResourceLocation selectedId = visible.stream()
                .filter(definition -> definition.selectedWhen().test(context))
                .map(TabDefinition::id)
                .findFirst()
                .orElse(null);
        if (selectedId == null && inventoryVisible && context.screen() instanceof InventoryScreen) {
            selectedId = GeminiTabs.INVENTORY_ID;
        }
        return new TabLayout(visible, inventoryVisible, Optional.ofNullable(selectedId));
    }

    public synchronized State state() {
        return this.state;
    }

    public synchronized int size() {
        return this.tabs.size();
    }

    public synchronized List<TabHandle> handles() {
        return this.tabs.values().stream().map(RegisteredTab::handle).toList();
    }

    private record RegisteredTab(TabHandle handle, TabDefinition definition) {
    }

    public enum State {
        COLLECTING,
        SEALED
    }
}

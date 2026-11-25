package dev.arakiel.gemini.tab.client.internal;

import dev.arakiel.gemini.mixin.tab.AbstractContainerScreenAccessor;
import dev.arakiel.gemini.mixin.tab.ScreenAccessor;
import dev.arakiel.gemini.tab.GeminiTabs;
import dev.arakiel.gemini.tab.TabContext;
import dev.arakiel.gemini.tab.TabDefinition;
import dev.arakiel.gemini.tab.TabLayout;
import dev.arakiel.gemini.tab.TabPlacement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class TabNavigator {
    private static final int MAX_SLOTS = 7;
    private static final int PAGE_CAPACITY = MAX_SLOTS - 1;

    private final Minecraft minecraft;
    private final AbstractContainerScreen<?> screen;
    private final TabContext context;
    private final TabLayout layout;
    private final TabPlacement placement = TabPlacement.ABOVE;
    private final TabSprites sprites = new TabSprites(this.placement);
    private final List<TabButton> tabs = new ArrayList<>();
    private Button previous;
    private Button next;
    private int firstTabX;
    private int firstTabY;
    private int page;
    private int pages;

    public TabNavigator(Minecraft minecraft, Screen screen, TabLayout layout) {
        if (!(screen instanceof AbstractContainerScreen<?> container)) {
            throw new IllegalArgumentException("Gemini tabs require an AbstractContainerScreen");
        }
        this.minecraft = minecraft;
        this.screen = container;
        this.context = new TabContext(screen, minecraft.player);
        this.layout = layout;
    }

    public void install() {
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this.screen;
        this.firstTabX = accessor.gemini$getGuiLeft();
        this.firstTabY = accessor.gemini$getGuiTop();
        this.createTabs();
        this.createPageButtons();
        this.page = this.initialPage();
        this.updateVisibility();
    }

    private void createTabs() {
        List<Entry> entries = new ArrayList<>();
        if (this.layout.inventoryVisible()) {
            boolean selected = this.layout.selectedId()
                    .filter(GeminiTabs.INVENTORY_ID::equals)
                    .isPresent();
            entries.add(new Entry(null, selected));
        }
        for (TabDefinition definition : this.layout.tabs()) {
            boolean selected = this.layout.selectedId()
                    .filter(definition.id()::equals)
                    .isPresent();
            entries.add(new Entry(definition, selected));
        }
        this.pages = Math.max(1, (entries.size() + PAGE_CAPACITY - 1) / PAGE_CAPACITY);
        for (int index = 0; index < entries.size(); index++) {
            Entry entry = entries.get(index);
            int pageIndex = index / PAGE_CAPACITY;
            int slot = index % PAGE_CAPACITY;
            int x = this.firstTabX + this.sprites.x(slot);
            int y = this.firstTabY + this.sprites.y(slot);
            Component title = entry.inventory()
                    ? Component.translatable(GeminiTabs.INVENTORY_TRANSLATION_KEY)
                    : entry.definition().title();
            TabButton button = new TabButton(
                    this,
                    entry.definition(),
                    this.sprites,
                    title,
                    entry.selected(),
                    slot,
                    pageIndex,
                    x,
                    y
            );
            this.tabs.add(button);
        ((ScreenAccessor) this.screen).gemini$addRenderableWidget(button);
        }
    }

    private void createPageButtons() {
        int radius = 3;
        int size = 26 - radius * 2;
        this.previous = ((ScreenAccessor) this.screen).gemini$addRenderableWidget(
                Button.builder(Component.literal("<"), button -> this.changePage(-1))
                        .pos(this.firstTabX - 26 + radius, this.firstTabY - 26 + radius)
                        .size(size, size)
                        .build()
        );
        this.next = ((ScreenAccessor) this.screen).gemini$addRenderableWidget(
                Button.builder(Component.literal(">"), button -> this.changePage(1))
                        .pos(this.firstTabX + (MAX_SLOTS - 1) * 26 + radius, this.firstTabY - 26 + radius)
                        .size(size, size)
                        .build()
        );
    }

    private int initialPage() {
        for (int index = 0; index < this.tabs.size(); index++) {
            if (this.tabs.get(index).selected()) {
                return index / PAGE_CAPACITY;
            }
        }
        return 0;
    }

    private void changePage(int direction) {
        this.page = Math.max(0, Math.min(this.pages - 1, this.page + direction));
        this.updateVisibility();
    }

    private void updateVisibility() {
        for (TabButton tab : this.tabs) {
            tab.visible = tab.active = tab.page() == this.page;
        }
        this.previous.visible = this.previous.active = this.page > 0;
        this.next.visible = this.next.active = this.page < this.pages - 1;
    }

    void activate(TabDefinition definition) {
        if (definition != null) {
            definition.action().open(this.context);
            return;
        }
        if (this.minecraft.player != null) {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        }
    }

    private record Entry(TabDefinition definition, boolean selected) {
        private boolean inventory() {
            return this.definition == null;
        }
    }
}

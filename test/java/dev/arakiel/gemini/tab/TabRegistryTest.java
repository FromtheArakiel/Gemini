package dev.arakiel.gemini.tab;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TabRegistryTest {
    private static final TabContext CONTEXT = new TabContext(null, null);

    @Test
    void inventoryStaysHiddenWithoutExternalTabs() {
        TabRegistry registry = new TabRegistry();

        TabLayout layout = registry.layout(CONTEXT);

        assertTrue(layout.isEmpty());
        assertFalse(layout.inventoryVisible());
        assertTrue(layout.tabs().isEmpty());
    }

    @Test
    void inventoryBecomesVisibleFirstWhenAnExternalTabExists() {
        TabRegistry registry = new TabRegistry();
        registry.register(tab("example:alpha", "example", TabOrder.anchored(0)));

        TabLayout layout = registry.layout(CONTEXT);

        assertTrue(layout.inventoryVisible());
        assertEquals(2, layout.totalSize());
        assertEquals(List.of("example:alpha"), ids(layout));
    }

    @Test
    void matchingAnchorAtZeroCannotDisplaceInventory() {
        TabRegistry registry = new TabRegistry();
        registry.register(tab("example:alpha", "example", TabOrder.anchored(0)));
        registry.register(tab("example:beta", "example", TabOrder.anchored(0)));

        TabLayout layout = registry.layout(CONTEXT);

        assertTrue(layout.inventoryVisible());
        assertEquals(List.of("example:alpha", "example:beta"), ids(layout));
    }

    @Test
    void anchoredCollisionsMoveForwardByIdentifierOrder() {
        TabRegistry registry = new TabRegistry();
        registry.register(tab("example:charlie", "example", TabOrder.anchored(1)));
        registry.register(tab("example:bravo", "example", TabOrder.anchored(1)));
        registry.register(tab("example:alpha", "example", TabOrder.anchored(1)));

        TabLayout layout = registry.layout(CONTEXT);

        assertEquals(List.of("example:alpha", "example:bravo", "example:charlie"), ids(layout));
    }

    @Test
    void queuedTabsFillGapsAroundAnchors() {
        TabRegistry registry = new TabRegistry();
        registry.register(tab("example:anchor", "example", TabOrder.anchored(2)));
        registry.register(tab("example:beta", "example", TabOrder.queued()));
        registry.register(tab("example:alpha", "example", TabOrder.queued()));

        TabLayout layout = registry.layout(CONTEXT);

        assertEquals(List.of("example:alpha", "example:beta", "example:anchor"), ids(layout));
    }

    @Test
    void queuedLanesRunBeforeIdentifierTieBreak() {
        TabRegistry registry = new TabRegistry();
        registry.register(tab("example:alpha", "example", TabOrder.queued(10)));
        registry.register(tab("example:zulu", "example", TabOrder.queued(1)));

        TabLayout layout = registry.layout(CONTEXT);

        assertEquals(List.of("example:zulu", "example:alpha"), ids(layout));
    }

    @Test
    void orderingDoesNotDependOnRegistrationOrder() {
        List<TabDefinition> definitions = List.of(
                tab("example:zulu", "example", TabOrder.queued(2)),
                tab("example:bravo", "example", TabOrder.anchored(1)),
                tab("example:alpha", "example", TabOrder.anchored(1)),
                tab("example:charlie", "example", TabOrder.queued(0))
        );
        TabRegistry forward = new TabRegistry();
        definitions.forEach(forward::register);
        TabRegistry reverse = new TabRegistry();
        definitions.reversed().forEach(reverse::register);

        assertEquals(ids(forward.layout(CONTEXT)), ids(reverse.layout(CONTEXT)));
    }

    @Test
    void duplicateFromSameOwnerIsIdempotent() {
        TabRegistry registry = new TabRegistry();
        TabRegistration first = registry.register(tab("example:alpha", "example", TabOrder.queued()));
        TabRegistration duplicate = registry.register(tab("example:alpha", "example", TabOrder.anchored(5)));

        assertEquals(TabRegistration.Status.REGISTERED, first.status());
        assertEquals(TabRegistration.Status.ALREADY_REGISTERED, duplicate.status());
        assertEquals(first.handle(), duplicate.handle());
        assertEquals(1, registry.size());
    }

    @Test
    void duplicateFromAnotherOwnerCannotStealCanonicalTab() {
        TabRegistry registry = new TabRegistry();
        TabRegistration first = registry.register(tab("example:alpha", "example", TabOrder.queued()));
        TabRegistration duplicate = registry.register(tab("example:alpha", "other", TabOrder.anchored(0)));

        assertEquals(TabRegistration.Status.CONFLICT, duplicate.status());
        assertEquals(first.handle().owner(), duplicate.handle().owner());
    }

    @Test
    void lateRegistrationIsRejectedAfterSeal() {
        TabRegistry registry = new TabRegistry();
        registry.layout(CONTEXT);

        TabRegistration late = registry.register(tab("example:late", "example", TabOrder.queued()));

        assertEquals(TabRegistration.Status.TOO_LATE, late.status());
        assertEquals(0, registry.size());
    }

    @Test
    void filteredTabsCompactWithoutChangingRelativeOrder() {
        TabRegistry registry = new TabRegistry();
        registry.register(tab("example:alpha", "example", TabOrder.queued(), false));
        registry.register(tab("example:bravo", "example", TabOrder.queued(), true));
        registry.register(tab("example:charlie", "example", TabOrder.queued(), true));

        TabLayout layout = registry.layout(CONTEXT);

        assertTrue(layout.inventoryVisible());
        assertEquals(List.of("example:bravo", "example:charlie"), ids(layout));
    }

    @Test
    void hidingEveryExternalTabAlsoHidesInventory() {
        TabRegistry registry = new TabRegistry();
        registry.register(tab("example:alpha", "example", TabOrder.queued(), false));

        TabLayout layout = registry.layout(CONTEXT);

        assertTrue(layout.isEmpty());
        assertFalse(layout.inventoryVisible());
    }

    @Test
    void selectedTabIsUniqueAndUsesTabOrder() {
        TabRegistry registry = new TabRegistry();
        registry.register(tab("example:beta", "example", TabOrder.queued(), context -> true));
        registry.register(tab("example:alpha", "example", TabOrder.queued(), context -> true));

        TabLayout layout = registry.layout(CONTEXT);

        assertEquals(
                List.of("example:alpha"),
                layout.selectedId().stream().map(ResourceLocation::toString).toList()
        );
    }

    @Test
    void hiddenSelectedTabCannotBecomeSelected() {
        TabRegistry registry = new TabRegistry();
        registry.register(tab("example:hidden", "example", TabOrder.queued(), false, context -> true));
        registry.register(tab("example:visible", "example", TabOrder.queued(), true, context -> false));

        TabLayout layout = registry.layout(CONTEXT);

        assertTrue(layout.selectedId().isEmpty());
    }

    private static TabDefinition tab(String id, String owner, TabOrder order) {
        return tab(id, owner, order, true);
    }

    private static TabDefinition tab(String id, String owner, TabOrder order, boolean visible) {
        return tab(id, owner, order, visible, context -> false);
    }

    private static TabDefinition tab(
            String id,
            String owner,
            TabOrder order,
            Predicate<TabContext> selectedWhen
    ) {
        return tab(id, owner, order, true, selectedWhen);
    }

    private static TabDefinition tab(
            String id,
            String owner,
            TabOrder order,
            boolean visible,
            Predicate<TabContext> selectedWhen
    ) {
        return TabDefinition.builder(ResourceLocation.parse(id))
                .owner(owner)
                .title(Component.literal(id))
                .icon((graphics, x, y) -> {
                })
                .action(context -> {
                })
                .order(order)
                .visibleWhen(context -> visible)
                .selectedWhen(selectedWhen)
                .build();
    }

    private static List<String> ids(TabLayout layout) {
        return layout.tabs().stream().map(tab -> tab.id().toString()).toList();
    }
}

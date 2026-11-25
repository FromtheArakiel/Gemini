package dev.arakiel.gemini.tab.internal;

import dev.arakiel.gemini.tab.TabDefinition;
import dev.arakiel.gemini.tab.TabOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class TabOrdering {
    private static final Comparator<TabDefinition> ID_TIE_BREAK =
            Comparator.comparing(tab -> tab.id().toString());

    private TabOrdering() {
    }

    public static List<TabDefinition> order(Collection<TabDefinition> definitions) {
        List<AnchoredTab> anchors = new ArrayList<>();
        List<TabDefinition> queue = new ArrayList<>();
        for (TabDefinition definition : definitions) {
            if (definition.order() instanceof TabOrder.Anchored anchored) {
                anchors.add(new AnchoredTab(definition, anchored.position()));
            } else {
                queue.add(definition);
            }
        }

        anchors.sort(Comparator.comparingInt(AnchoredTab::position).thenComparing(AnchoredTab::definition, ID_TIE_BREAK));
        queue.sort(Comparator
                .comparingInt(TabOrdering::queueLane)
                .thenComparing(tab -> tab.id().toString()));

        Map<Integer, TabDefinition> occupied = new TreeMap<>();
        int previous = -1;
        for (AnchoredTab anchor : anchors) {
            int position = Math.max(anchor.position(), previous + 1);
            while (occupied.containsKey(position)) {
                position++;
            }
            occupied.put(position, anchor.definition());
            previous = position;
        }

        int nextFree = 0;
        for (TabDefinition definition : queue) {
            while (occupied.containsKey(nextFree)) {
                nextFree++;
            }
            occupied.put(nextFree, definition);
            nextFree++;
        }
        return List.copyOf(new ArrayList<>(occupied.values()));
    }

    private static int queueLane(TabDefinition definition) {
        return definition.order() instanceof TabOrder.Queued queued ? queued.lane() : 0;
    }

    private record AnchoredTab(TabDefinition definition, int position) {
    }
}

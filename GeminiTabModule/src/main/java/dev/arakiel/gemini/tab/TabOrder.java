package dev.arakiel.gemini.tab;

public sealed interface TabOrder permits TabOrder.Anchored, TabOrder.Queued {
    static TabOrder anchored(int position) {
        return new Anchored(position);
    }

    static TabOrder queued() {
        return new Queued(0);
    }

    static TabOrder queued(int lane) {
        return new Queued(lane);
    }

    record Anchored(int position) implements TabOrder {
        public Anchored {
            if (position < 0) {
                throw new IllegalArgumentException("Tab anchor must not be negative");
            }
        }
    }

    record Queued(int lane) implements TabOrder {
    }
}

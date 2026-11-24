package dev.arakiel.gemini.tab;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Predicate;

public record TabDefinition(
        ResourceLocation id,
        String owner,
        Component title,
        TabIcon icon,
        TabAction action,
        TabOrder order,
        Predicate<TabContext> visibleWhen,
        Predicate<TabContext> selectedWhen
) {
    public TabDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(visibleWhen, "visibleWhen");
        Objects.requireNonNull(selectedWhen, "selectedWhen");
        if (owner.isBlank()) {
            throw new IllegalArgumentException("Tab owner must not be blank");
        }
    }

    public static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final ResourceLocation id;
        private String owner;
        private Component title;
        private TabIcon icon;
        private TabAction action;
        private TabOrder order = TabOrder.queued();
        private Predicate<TabContext> visibleWhen = context -> true;
        private Predicate<TabContext> selectedWhen = context -> false;

        private Builder(ResourceLocation id) {
            this.id = Objects.requireNonNull(id, "id");
        }

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder icon(TabIcon icon) {
            this.icon = icon;
            return this;
        }

        public Builder action(TabAction action) {
            this.action = action;
            return this;
        }

        public Builder order(TabOrder order) {
            this.order = order;
            return this;
        }

        public Builder visibleWhen(Predicate<TabContext> visibleWhen) {
            this.visibleWhen = visibleWhen;
            return this;
        }

        public Builder selectedWhen(Predicate<TabContext> selectedWhen) {
            this.selectedWhen = selectedWhen;
            return this;
        }

        public TabDefinition build() {
            return new TabDefinition(
                    this.id,
                    this.owner,
                    this.title,
                    this.icon,
                    this.action,
                    this.order,
                    this.visibleWhen,
                    this.selectedWhen
            );
        }
    }
}

package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class RegisterRecipeBookCategoriesEvent extends GeminiEvent {
    private final List<Registration<?>> registrations = new ArrayList<>();
    private final List<AggregateRegistration> aggregateRegistrations = new ArrayList<>();
    private final List<BookRegistration> bookRegistrations = new ArrayList<>();
    private final List<FinderRegistration<?>> finderRegistrations = new ArrayList<>();

    public <T extends net.minecraft.world.item.crafting.Recipe<?>> void register(
            RecipeType<T> type,
            RecipeBookCategories categories
    ) {
        this.registrations.add(new Registration<>(type, categories));
    }

    public List<Registration<?>> getRegistrations() {
        return List.copyOf(this.registrations);
    }

    public void registerAggregateCategory(
            RecipeBookCategories category,
            List<RecipeBookCategories> aggregate
    ) {
        this.aggregateRegistrations.add(new AggregateRegistration(category, List.copyOf(aggregate)));
    }

    public void registerBookCategories(RecipeBookType type, List<RecipeBookCategories> categories) {
        this.bookRegistrations.add(new BookRegistration(type, List.copyOf(categories)));
    }

    public void registerRecipeCategoryFinder(
            RecipeType<?> type,
            Function<RecipeHolder<?>, RecipeBookCategories> lookup
    ) {
        this.finderRegistrations.add(new FinderRegistration<>(type, lookup));
    }

    public List<AggregateRegistration> getAggregateRegistrations() {
        return List.copyOf(this.aggregateRegistrations);
    }

    public List<BookRegistration> getBookRegistrations() {
        return List.copyOf(this.bookRegistrations);
    }

    public List<FinderRegistration<?>> getFinderRegistrations() {
        return List.copyOf(this.finderRegistrations);
    }

    public record Registration<T extends net.minecraft.world.item.crafting.Recipe<?>>(
            RecipeType<T> type,
            RecipeBookCategories categories
    ) {
        public Registration {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(categories, "categories");
        }
    }

    public record AggregateRegistration(
            RecipeBookCategories category,
            List<RecipeBookCategories> categories
    ) {
        public AggregateRegistration {
            Objects.requireNonNull(category, "category");
            Objects.requireNonNull(categories, "categories");
        }
    }

    public record BookRegistration(RecipeBookType type, List<RecipeBookCategories> categories) {
        public BookRegistration {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(categories, "categories");
        }
    }

    public record FinderRegistration<T extends net.minecraft.world.item.crafting.Recipe<?>>(
            RecipeType<T> type,
            Function<RecipeHolder<?>, RecipeBookCategories> lookup
    ) {
        public FinderRegistration {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(lookup, "lookup");
        }
    }
}

package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Objects;

public class RecipesUpdatedEvent extends GeminiEvent {
    private final RecipeManager recipeManager;

    public RecipesUpdatedEvent(RecipeManager recipeManager) {
        this.recipeManager = Objects.requireNonNull(recipeManager, "recipeManager");
    }

    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }
}

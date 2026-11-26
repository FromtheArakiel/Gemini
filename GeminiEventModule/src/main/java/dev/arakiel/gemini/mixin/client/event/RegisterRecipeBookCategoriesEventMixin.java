package dev.arakiel.gemini.mixin.client.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.RegisterRecipeBookCategoriesEvent;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mixin(RecipeBookCategories.class)
public abstract class RegisterRecipeBookCategoriesEventMixin {
    @Shadow
    @Final
    @Mutable
    public static Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES;

    @Unique
    private static final Map<RecipeBookType, List<RecipeBookCategories>> GEMINI$BOOK_CATEGORIES =
            new LinkedHashMap<>();

    @Unique
    private static boolean gemini$categoriesApplied;

    @Unique
    private static void gemini$applyCategories() {
        if (gemini$categoriesApplied) {
            return;
        }
        gemini$categoriesApplied = true;
        RegisterRecipeBookCategoriesEvent event = new RegisterRecipeBookCategoriesEvent();
        GeminiEvents.post(event);
        Map<RecipeBookCategories, List<RecipeBookCategories>> aggregate =
                new LinkedHashMap<>(AGGREGATE_CATEGORIES);
        for (RegisterRecipeBookCategoriesEvent.AggregateRegistration registration
                : event.getAggregateRegistrations()) {
            aggregate.put(registration.category(), new ArrayList<>(registration.categories()));
        }
        AGGREGATE_CATEGORIES = aggregate;
        for (RegisterRecipeBookCategoriesEvent.BookRegistration registration : event.getBookRegistrations()) {
            GEMINI$BOOK_CATEGORIES.put(registration.type(), new ArrayList<>(registration.categories()));
        }
    }

    @Inject(method = "getCategories", at = @At("HEAD"), cancellable = true)
    private static void gemini$getCategories(
            RecipeBookType type,
            CallbackInfoReturnable<List<RecipeBookCategories>> callback
    ) {
        gemini$applyCategories();
        List<RecipeBookCategories> categories = GEMINI$BOOK_CATEGORIES.get(type);
        if (categories != null) {
            callback.setReturnValue(List.copyOf(categories));
        }
    }
}

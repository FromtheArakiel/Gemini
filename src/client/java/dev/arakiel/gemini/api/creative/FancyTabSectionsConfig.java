package dev.arakiel.gemini.api.creative;

public final class FancyTabSectionsConfig {
    private static boolean indexExpanded = true;

    private FancyTabSectionsConfig() {
    }

    public static boolean indexExpanded() {
        return indexExpanded;
    }

    public static void setIndexExpanded(boolean expanded) {
        indexExpanded = expanded;
    }
}

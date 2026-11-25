package dev.arakiel.gemini.capability.internal;

import dev.arakiel.gemini.capability.CapabilityContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CapabilityLookupInternals {
    private CapabilityLookupInternals() {
    }

    public static List<CapabilityRegistration<?>> order(
            List<CapabilityRegistration<?>> registrations,
            CapabilityContext context
    ) {
        List<CapabilityRegistration<?>> matches = new ArrayList<>();
        for (CapabilityRegistration<?> registration : registrations) {
            if (registration.source().matches(context)) {
                matches.add(registration);
            }
        }
        matches.sort(Comparator
                .comparingInt((CapabilityRegistration<?> registration) ->
                        registration.source().specificity(context))
                .thenComparing(Comparator.comparing(CapabilityRegistration<?>::priority).reversed())
                .thenComparingLong(CapabilityRegistration<?>::order));
        return matches;
    }
}

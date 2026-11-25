package dev.arakiel.gemini.integration.internal;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

import java.util.Objects;

public final class GeminiIntegrationVersion {
    private final VersionPredicate predicate;

    private GeminiIntegrationVersion(VersionPredicate predicate) {
        this.predicate = predicate;
    }

    public static GeminiIntegrationVersion parse(String specification) {
        String normalized = normalize(Objects.requireNonNull(specification, "specification").trim());
        try {
            return new GeminiIntegrationVersion(VersionPredicate.parse(normalized));
        } catch (VersionParsingException exception) {
            throw new IllegalArgumentException("Invalid integration version range: " + specification, exception);
        }
    }

    public boolean matches(Version version) {
        return predicate.test(version);
    }

    public boolean matches(String version) {
        try {
            return matches(Version.parse(version));
        } catch (VersionParsingException exception) {
            return false;
        }
    }

    static String normalize(String specification) {
        if (specification.isEmpty() || specification.equals("*")) {
            return "*";
        }
        char first = specification.charAt(0);
        char last = specification.charAt(specification.length() - 1);
        if ((first != '[' && first != '(') || (last != ']' && last != ')')) {
            return specification;
        }
        String body = specification.substring(1, specification.length() - 1);
        String[] bounds = body.split(",", -1);
        if (bounds.length != 2) {
            throw new IllegalArgumentException("Invalid interval: " + specification);
        }
        String lower = bounds[0].trim();
        String upper = bounds[1].trim();
        if (lower.isEmpty() && upper.isEmpty()) {
            return "*";
        }
        if (!lower.isEmpty() && lower.equals(upper) && first == '[' && last == ']') {
            return "=" + lower;
        }
        StringBuilder result = new StringBuilder();
        if (!lower.isEmpty()) {
            result.append(first == '[' ? ">=" : ">").append(lower);
        }
        if (!upper.isEmpty()) {
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(last == ']' ? "<=" : "<").append(upper);
        }
        return result.toString();
    }
}

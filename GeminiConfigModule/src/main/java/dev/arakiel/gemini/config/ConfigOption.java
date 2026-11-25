package dev.arakiel.gemini.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record ConfigOption(
        String path,
        String translationKey,
        String comment,
        ConfigValueType type,
        Object value,
        Object defaultValue,
        double min,
        double max,
        List<String> choices,
        boolean hidden,
        boolean restartRequired
) {
    public ConfigOption {
        path = Objects.requireNonNull(path, "path");
        translationKey = Objects.requireNonNull(translationKey, "translationKey");
        comment = Objects.requireNonNull(comment, "comment");
        type = Objects.requireNonNull(type, "type");
        value = copyValue(value);
        defaultValue = copyValue(defaultValue);
        choices = List.copyOf(choices);
    }

    public boolean isNumeric() {
        return type == ConfigValueType.INTEGER
                || type == ConfigValueType.LONG
                || type == ConfigValueType.FLOAT
                || type == ConfigValueType.DOUBLE;
    }

    public boolean isList() {
        return type == ConfigValueType.BOOLEAN_LIST
                || type == ConfigValueType.INTEGER_LIST
                || type == ConfigValueType.LONG_LIST
                || type == ConfigValueType.DOUBLE_LIST
                || type == ConfigValueType.STRING_LIST;
    }

    private static Object copyValue(Object value) {
        if (value instanceof List<?> list) {
            return List.copyOf(new ArrayList<>(list));
        }
        return value;
    }
}

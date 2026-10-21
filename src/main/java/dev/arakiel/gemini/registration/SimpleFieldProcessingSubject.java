package dev.arakiel.gemini.registration;

import java.lang.reflect.Field;

public interface SimpleFieldProcessingSubject<T> extends FieldProcessingSubject<T> {
    void processField(T value, String identifier, Field field);
}

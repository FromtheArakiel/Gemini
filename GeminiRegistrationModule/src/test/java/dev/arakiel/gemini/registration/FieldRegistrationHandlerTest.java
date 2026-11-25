package dev.arakiel.gemini.registration;

import dev.arakiel.gemini.registration.AutoRegistryContainer;
import dev.arakiel.gemini.registration.FieldProcessingSubject;
import dev.arakiel.gemini.registration.RegistrySink;
import dev.arakiel.gemini.registration.SimpleFieldProcessingSubject;
import dev.arakiel.gemini.registration.annotations.AssignedName;
import dev.arakiel.gemini.registration.annotations.IterationIgnored;
import dev.arakiel.gemini.registration.annotations.RegistryNamespace;
import dev.arakiel.gemini.registration.internal.FieldRegistrationHandler;
import net.minecraft.core.Registry;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldRegistrationHandlerTest {
    private static final Map<String, String> REGISTERED = new LinkedHashMap<>();

    @Test
    void scanFieldsFiltersAndRenames() {
        List<String> names = new ArrayList<>();
        List<String> values = new ArrayList<>();

        FieldRegistrationHandler.scanFields(ProcessingContainer.class, String.class, (value, name, field) -> {
            names.add(name);
            values.add(value);
        });

        assertEquals(List.of("first", "renamed"), names);
        assertEquals(List.of("first", "second"), values);
    }

    @Test
    void processUsesSubjectFilterAndAfterHook() {
        List<String> processed = new ArrayList<>();
        ProcessingContainer.afterCalls = 0;

        FieldRegistrationHandler.process(ProcessingContainer.class, (value, name, field) -> {
            processed.add(name + "=" + value);
        }, false);

        assertEquals(List.of("renamed=second"), processed);
        assertEquals(1, ProcessingContainer.afterCalls);
    }

    @Test
    void processSimpleDelegatesToSubject() {
        SimpleContainer.processed.clear();
        FieldRegistrationHandler.processSimple(SimpleContainer.class, false);

        assertEquals(List.of("simple", "aliased"), SimpleContainer.processed);
    }

    @Test
    void registerFeedsSinkWithNamespaceAndAssignedName() {
        REGISTERED.clear();
        FieldRegistrationHandler.register(RegistrationContainer.class, "gemini_test", false);

        assertEquals("plain", REGISTERED.get("gemini_test:plain"));
        assertEquals("aliased", REGISTERED.get("gemini_test:alias"));
        assertEquals(2, REGISTERED.size());
    }

    @Test
    void registerRecursesIntoNestedContainersWithNamespaceOverride() {
        REGISTERED.clear();
        FieldRegistrationHandler.register(OuterContainer.class, "outer", true);

        assertTrue(REGISTERED.containsKey("outer:outer"));
        assertTrue(REGISTERED.containsKey("nested:inner"));
    }

    @Test
    void fieldNameDefaultsToLowerCase() throws NoSuchFieldException {
        Field field = ProcessingContainer.class.getField("FIRST");
        assertEquals("first", FieldRegistrationHandler.getFieldName(field));
    }

    static final class ProcessingContainer implements FieldProcessingSubject<String> {
        public static final String FIRST = "first";
        @AssignedName("renamed")
        public static final String SECOND = "second";
        @IterationIgnored
        public static final String SKIPPED = "skipped";
        public static final Integer NUMBER = 7;
        static final String PACKAGE_PRIVATE = "package";
        public final String instance = "instance";

        static int afterCalls;

        @Override
        public Class<String> getTargetFieldType() {
            return String.class;
        }

        @Override
        public boolean shouldProcessField(String value, String identifier, Field field) {
            return !value.equals("first");
        }

        @Override
        public void afterFieldProcessing() {
            afterCalls++;
        }
    }

    static final class SimpleContainer implements SimpleFieldProcessingSubject<String> {
        public static final String SIMPLE = "simple";
        @AssignedName("aliased")
        public static final String ALIAS = "aliased";

        static final List<String> processed = new ArrayList<>();

        @Override
        public Class<String> getTargetFieldType() {
            return String.class;
        }

        @Override
        public void processField(String value, String identifier, Field field) {
            processed.add(identifier);
        }
    }

    static final class RegistrationContainer implements AutoRegistryContainer<String> {
        public static final String PLAIN = "plain";
        @AssignedName("alias")
        public static final String ALIAS = "aliased";
        @IterationIgnored
        public static final String SKIPPED = "skipped";

        @Override
        public Registry<String> getRegistry() {
            return null;
        }

        @Override
        public Class<String> getTargetFieldType() {
            return String.class;
        }

        @Override
        public RegistrySink<String> getSink() {
            return (id, value) -> {
                REGISTERED.put(id.toString(), value);
                return value;
            };
        }
    }

    static final class OuterContainer implements AutoRegistryContainer<String> {
        public static final String OUTER = "outer";

        @Override
        public Registry<String> getRegistry() {
            return null;
        }

        @Override
        public Class<String> getTargetFieldType() {
            return String.class;
        }

        @Override
        public RegistrySink<String> getSink() {
            return (id, value) -> {
                REGISTERED.put(id.toString(), value);
                return value;
            };
        }

        @RegistryNamespace("nested")
        static final class InnerContainer implements AutoRegistryContainer<String> {
            public static final String INNER = "inner";

            @Override
            public Registry<String> getRegistry() {
                return null;
            }

            @Override
            public Class<String> getTargetFieldType() {
                return String.class;
            }

            @Override
            public RegistrySink<String> getSink() {
                return (id, value) -> {
                    REGISTERED.put(id.toString(), value);
                    return value;
                };
            }
        }
    }
}

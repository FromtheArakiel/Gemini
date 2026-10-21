package dev.arakiel.gemini.integration.internal;

import dev.arakiel.gemini.integration.GeminiIntegration;
import dev.arakiel.gemini.integration.GeminiIntegrationType;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

final class GeminiIntegrationScanner {
    private static final String DESCRIPTOR = "L" + GeminiIntegration.class.getName().replace('.', '/') + ";";

    private GeminiIntegrationScanner() {
    }

    static Optional<GeminiIntegrationEntry> scan(Path classFile) {
        try (InputStream input = Files.newInputStream(classFile)) {
            ClassReader reader = new ClassReader(input);
            AnnotationData[] data = new AnnotationData[1];
            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (!DESCRIPTOR.equals(descriptor)) {
                        return null;
                    }
                    AnnotationData annotation = new AnnotationData(reader.getClassName().replace('/', '.'));
                    data[0] = annotation;
                    return annotation;
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            if (data[0] == null || data[0].modId == null || data[0].modId.isBlank()) {
                return Optional.empty();
            }
            GeminiIntegrationVersion version = GeminiIntegrationVersion.parse(data[0].version);
            List<GeminiIntegrationType> types = data[0].types.isEmpty()
                    ? List.of(GeminiIntegrationType.COMMON)
                    : List.copyOf(data[0].types);
            return Optional.of(new GeminiIntegrationEntry(data[0].modId, data[0].className, version, types));
        } catch (IOException | RuntimeException exception) {
            return Optional.empty();
        }
    }

    static Stream<Path> classFiles(Path root) {
        try {
            return Files.walk(root).filter(path -> Files.isRegularFile(path)
                    && path.getFileName().toString().endsWith(".class"));
        } catch (IOException exception) {
            return Stream.empty();
        }
    }

    private static final class AnnotationData extends AnnotationVisitor {
        private final String className;
        private String modId;
        private String version = "*";
        private final List<GeminiIntegrationType> types = new ArrayList<>();

        private AnnotationData(String className) {
            super(Opcodes.ASM9);
            this.className = className;
        }

        @Override
        public void visit(String name, Object value) {
            if ("value".equals(name)) {
                modId = value.toString();
            } else if ("version".equals(name)) {
                version = value.toString();
            }
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            if (!"type".equals(name)) {
                return super.visitArray(name);
            }
            return new AnnotationVisitor(Opcodes.ASM9) {
                @Override
                public void visitEnum(String ignored, String descriptor, String value) {
                    types.add(GeminiIntegrationType.valueOf(value));
                }
            };
        }
    }
}

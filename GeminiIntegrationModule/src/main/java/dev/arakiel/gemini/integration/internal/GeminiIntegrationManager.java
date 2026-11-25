package dev.arakiel.gemini.integration.internal;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.integration.GeminiIntegrationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class GeminiIntegrationManager {
    private final Map<String, GeminiIntegrationEntry> integrations = new LinkedHashMap<>();
    private final Map<String, Object> instances = new ConcurrentHashMap<>();
    private final Set<String> activeMods = ConcurrentHashMap.newKeySet();
    private final Set<GeminiIntegrationType> initializedStages = new HashSet<>();
    private boolean scanned;

    public synchronized void initialize(GeminiIntegrationType stage) {
        if (!initializedStages.add(stage)) {
            return;
        }
        scan();

        FabricLoader loader = FabricLoader.getInstance();
        boolean client = loader.getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT;
        for (GeminiIntegrationEntry integration : integrations.values()) {
            if (!appliesTo(stage, integration.types(), client)) {
                continue;
            }
            ModContainer mod = findMod(loader, integration.modId());
            if (mod == null || !integration.version().matches(mod.getMetadata().getVersion())) {
                continue;
            }
            invoke(integration, stage, mod);
        }
    }

    public Set<String> activeMods() {
        return Collections.unmodifiableSet(activeMods);
    }

    private void scan() {
        if (scanned) {
            return;
        }
        scanned = true;
        FabricLoader loader = FabricLoader.getInstance();
        Set<String> visited = new HashSet<>();

        for (ModContainer container : loader.getAllMods()) {
            for (Path root : container.getRootPaths()) {
                try (var classes = GeminiIntegrationScanner.classFiles(root)) {
                    classes.forEach(path -> GeminiIntegrationScanner.scan(path)
                            .filter(integration -> visited.add(integration.modId() + "\0" + integration.className()))
                            .ifPresent(integration -> integrations.put(integration.className(), integration)));
                } catch (RuntimeException exception) {
                    Gemini.LOGGER.debug("Unable to scan integration classes in {}", root, exception);
                }
            }
        }
    }

    private void invoke(GeminiIntegrationEntry integration, GeminiIntegrationType stage, ModContainer mod) {
        try {
            Object instance = instance(integration);
            String methodName = switch (stage) {
                case COMMON, SERVER -> "apply";
                case CLIENT -> "applyClient";
                case DATA -> "applyData";
            };
            Method method = findMethod(instance.getClass(), methodName);
            if (method == null && stage == GeminiIntegrationType.SERVER) {
                method = findMethod(instance.getClass(), "applyServer");
            }
            if (method == null) {
                Gemini.LOGGER.debug(
                        "GeminiIntegration {} for {} has no {} lifecycle method",
                        integration.className(),
                        integration.modId(),
                        methodName
                );
                return;
            }
            method.invoke(instance);
            activeMods.add(integration.modId());
            Gemini.LOGGER.info(
                    "Loaded {} integration for {} {}",
                    stage,
                    mod.getMetadata().getName(),
                    mod.getMetadata().getVersion().getFriendlyString()
            );
        } catch (InvocationTargetException exception) {
            Gemini.LOGGER.error("GeminiIntegration {} failed during {}", integration.className(), stage, exception.getCause());
        } catch (ReflectiveOperationException | LinkageError exception) {
            Gemini.LOGGER.error("Unable to load integration {} for {}", integration.className(), integration.modId(), exception);
        }
    }

    private Object instance(GeminiIntegrationEntry integration) throws ReflectiveOperationException {
        Object existing = instances.get(integration.className());
        if (existing != null) {
            return existing;
        }
        Class<?> type = Class.forName(integration.className(), true, Gemini.class.getClassLoader());
        Object created = type.getDeclaredConstructor().newInstance();
        Object raced = instances.putIfAbsent(integration.className(), created);
        return raced == null ? created : raced;
    }

    private static Method findMethod(Class<?> type, String name) {
        try {
            Method method = type.getDeclaredMethod(name);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }

    private static ModContainer findMod(FabricLoader loader, String modId) {
        return loader.getAllMods().stream()
                .filter(container -> provides(container.getMetadata(), modId))
                .findFirst()
                .orElse(null);
    }

    private static boolean provides(ModMetadata metadata, String modId) {
        return metadata.getId().equals(modId) || metadata.getProvides().contains(modId);
    }

    private static boolean appliesTo(GeminiIntegrationType stage, List<GeminiIntegrationType> types, boolean client) {
        if (stage == GeminiIntegrationType.COMMON) {
            if (types.contains(GeminiIntegrationType.COMMON)) {
                return true;
            }
            return !client && types.contains(GeminiIntegrationType.SERVER);
        }
        return types.contains(stage);
    }
}

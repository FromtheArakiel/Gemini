package dev.arakiel.gemini.integration.internal;

import dev.arakiel.gemini.integration.GeminiIntegrationType;

import java.util.List;

record GeminiIntegrationEntry(
        String modId,
        String className,
        GeminiIntegrationVersion version,
        List<GeminiIntegrationType> types
) {
}

package dev.arakiel.gemini.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiApiTest {
    @Test
    void fastUseMarkerDefaultsToEnabled() {
        GeminiApi.FastUseItem marker = new GeminiApi.FastUseItem() {
        };

        assertTrue(marker.isFastUse(null));
    }

    @Test
    void explosionEntityDefaultsAllowAllEntities() {
        assertTrue(GeminiApi.Explosion.EntityBehavior.all().shouldDamage(null));
    }
}

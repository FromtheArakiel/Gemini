package dev.arakiel.gemini.explosion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiExplosionTest {
    @Test
    void defaultEntityBehaviorAllowsAllEntities() {
        assertTrue(GeminiExplosion.EntityBehavior.all().shouldDamage(null));
    }
}

package dev.arakiel.gemini.tweak;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FastUseItemTest {
    @Test
    void markerDefaultsToEnabled() {
        FastUseItem marker = new FastUseItem() {
        };

        assertTrue(marker.isFastUse(null));
    }
}

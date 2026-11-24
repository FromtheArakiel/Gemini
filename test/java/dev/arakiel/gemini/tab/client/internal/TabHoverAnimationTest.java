package dev.arakiel.gemini.tab.client.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TabHoverAnimationTest {
    @Test
    void hoverBrightnessRampsAndRampsBackDown() {
        TabHoverAnimation animation = new TabHoverAnimation();

        assertEquals(1.0F, animation.brightness(false, 0L));
        float halfway = animation.brightness(true, 60_000_000L);
        float fullyHovered = animation.brightness(true, 120_000_000L);
        float halfwayBack = animation.brightness(false, 180_000_000L);
        float fullyReleased = animation.brightness(false, 240_000_000L);

        assertTrue(halfway > 1.0F && halfway < 1.12F);
        assertEquals(1.12F, fullyHovered, 0.0001F);
        assertTrue(halfwayBack > 1.0F && halfwayBack < 1.12F);
        assertEquals(1.0F, fullyReleased, 0.0001F);
    }
}

package dev.arakiel.gemini.tweak;

import dev.arakiel.gemini.tweak.internal.GeminiTweaksImpl;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.AbstractArrow;

import java.util.Objects;

public final class GeminiTweaks {
    private GeminiTweaks() {
    }

    public static void makeLightningSafe(LightningBolt bolt, boolean safe) {
        GeminiTweaksImpl.makeLightningSafe(Objects.requireNonNull(bolt, "bolt"), safe);
    }

    public static boolean isLightningSafe(LightningBolt bolt) {
        return GeminiTweaksImpl.isLightningSafe(bolt);
    }

    public static void setArrowDespawnFactor(AbstractArrow arrow, int factor) {
        GeminiTweaksImpl.setArrowDespawnFactor(Objects.requireNonNull(arrow, "arrow"), factor);
    }

    public static int getArrowDespawnFactor(AbstractArrow arrow) {
        return GeminiTweaksImpl.getArrowDespawnFactor(arrow);
    }
}

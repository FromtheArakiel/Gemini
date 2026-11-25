package dev.arakiel.gemini.tweak.internal;

import dev.arakiel.gemini.Gemini;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.AbstractArrow;

public final class GeminiTweaksImpl {
    private static final String SAFE_LIGHTNING_TAG = Gemini.MOD_ID + ":safe_lightning";
    private static final String DESPAWN_FACTOR = Gemini.MOD_ID + ":despawn_factor";

    private GeminiTweaksImpl() {
    }

    public static void makeLightningSafe(LightningBolt bolt, boolean safe) {
        if (safe) {
            bolt.addTag(SAFE_LIGHTNING_TAG);
        } else {
            bolt.removeTag(SAFE_LIGHTNING_TAG);
        }
    }

    public static boolean isLightningSafe(LightningBolt bolt) {
        return bolt.getTags().contains(SAFE_LIGHTNING_TAG);
    }

    public static void setArrowDespawnFactor(AbstractArrow arrow, int factor) {
        if (factor < 1) {
            throw new IllegalArgumentException("Arrow despawn factor must be at least 1");
        }
        arrow.getTags().removeIf(tag -> tag.startsWith(DESPAWN_FACTOR + "="));
        if (factor > 1) {
            arrow.addTag(DESPAWN_FACTOR + "=" + factor);
        }
    }

    public static int getArrowDespawnFactor(AbstractArrow arrow) {
        for (String tag : arrow.getTags()) {
            if (tag.startsWith(DESPAWN_FACTOR + "=")) {
                try {
                    return Integer.parseInt(tag.substring(DESPAWN_FACTOR.length() + 1));
                } catch (NumberFormatException ignored) {
                    return 1;
                }
            }
        }
        return 1;
    }
}

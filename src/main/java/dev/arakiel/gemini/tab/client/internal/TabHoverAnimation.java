package dev.arakiel.gemini.tab.client.internal;

final class TabHoverAnimation {
    private static final long FADE_DURATION_NANOS = 120_000_000L;
    private static final float HOVER_EXTRA_BRIGHTNESS = 0.12F;

    private float progress;
    private long lastUpdateNanos;
    private boolean initialized;

    float brightness(boolean hovered, long nowNanos) {
        float target = hovered ? 1.0F : 0.0F;
        if (!this.initialized) {
            this.initialized = true;
            this.lastUpdateNanos = nowNanos;
        } else if (nowNanos > this.lastUpdateNanos) {
            float elapsed = (nowNanos - this.lastUpdateNanos) / (float) FADE_DURATION_NANOS;
            if (this.progress < target) {
                this.progress = Math.min(target, this.progress + elapsed);
            } else if (this.progress > target) {
                this.progress = Math.max(target, this.progress - elapsed);
            }
            this.lastUpdateNanos = nowNanos;
        }
        float eased = this.progress * this.progress * (3.0F - 2.0F * this.progress);
        return 1.0F + HOVER_EXTRA_BRIGHTNESS * eased;
    }
}

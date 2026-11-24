package dev.arakiel.gemini.tab;

public enum TabPlacement {
    ABOVE(26, 32),
    BELOW(26, 32),
    LEFT(32, 28),
    RIGHT(32, 28);

    private final int width;
    private final int height;

    TabPlacement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }
}

package dev.arakiel.gemini.tab;

@FunctionalInterface
public interface TabAction {
    void open(TabContext context);
}

package dev.arakiel.gemini.tab;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;

public record TabContext(Screen screen, LocalPlayer player) {
}

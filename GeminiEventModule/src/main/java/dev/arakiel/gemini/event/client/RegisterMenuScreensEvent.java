package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterMenuScreensEvent extends GeminiEvent {
    private final List<Registration<?, ?>> registrations = new ArrayList<>();

    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
            MenuType<? extends M> type,
            MenuScreens.ScreenConstructor<M, U> constructor
    ) {
        this.registrations.add(new Registration<>(type, constructor));
    }

    public List<Registration<?, ?>> getRegistrations() {
        return List.copyOf(this.registrations);
    }

    public record Registration<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>(
            MenuType<? extends M> type,
            MenuScreens.ScreenConstructor<M, U> constructor
    ) {
        public Registration {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(constructor, "constructor");
        }
    }
}

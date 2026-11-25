package dev.arakiel.gemini.event.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;

public class RegisterCommandsEvent extends GeminiEvent {
    private final MinecraftServer server;
    private final CommandDispatcher<CommandSourceStack> dispatcher;
    private final RegistryAccess registries;
    private final Commands.CommandSelection selection;

    public RegisterCommandsEvent(
            MinecraftServer server,
            CommandDispatcher<CommandSourceStack> dispatcher,
            RegistryAccess registries,
            Commands.CommandSelection selection
    ) {
        this.server = Objects.requireNonNull(server, "server");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
        this.registries = Objects.requireNonNull(registries, "registries");
        this.selection = Objects.requireNonNull(selection, "selection");
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.dispatcher;
    }

    public RegistryAccess getRegistries() {
        return this.registries;
    }

    public Commands.CommandSelection getSelection() {
        return this.selection;
    }
}

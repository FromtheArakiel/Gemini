package dev.arakiel.gemini.event.client;

import com.mojang.brigadier.CommandDispatcher;
import dev.arakiel.gemini.event.GeminiEvent;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Objects;

public class RegisterClientCommandsEvent extends GeminiEvent {
    private final CommandDispatcher<FabricClientCommandSource> dispatcher;

    public RegisterClientCommandsEvent(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher");
    }

    public CommandDispatcher<FabricClientCommandSource> getDispatcher() {
        return this.dispatcher;
    }
}

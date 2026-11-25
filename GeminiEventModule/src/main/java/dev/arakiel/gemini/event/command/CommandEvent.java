package dev.arakiel.gemini.event.command;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.commands.CommandSourceStack;

import java.util.Objects;

@Cancelable
public class CommandEvent extends GeminiEvent {
    public enum Phase {
        PARSE,
        EXECUTE
    }

    private final CommandSourceStack source;
    private final String command;
    private final Phase phase;
    private int result;

    public CommandEvent(CommandSourceStack source, String command, Phase phase, int result) {
        this.source = Objects.requireNonNull(source, "source");
        this.command = Objects.requireNonNull(command, "command");
        this.phase = Objects.requireNonNull(phase, "phase");
        this.result = result;
    }

    public CommandSourceStack getSource() {
        return this.source;
    }

    public String getCommand() {
        return this.command;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public int getCommandResult() {
        return this.result;
    }

    public void setCommandResult(int result) {
        this.result = result;
    }
}

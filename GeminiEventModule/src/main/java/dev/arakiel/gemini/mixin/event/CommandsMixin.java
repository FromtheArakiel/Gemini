package dev.arakiel.gemini.mixin.event;

import com.mojang.brigadier.ParseResults;
import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.command.CommandEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public abstract class CommandsMixin {
    @Inject(method = "performPrefixedCommand", at = @At("HEAD"), cancellable = true)
    private void gemini$parseCommand(
            CommandSourceStack source,
            String command,
            CallbackInfo callback
    ) {
        CommandEvent event = new CommandEvent(source, command, CommandEvent.Phase.PARSE, 0);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "performCommand", at = @At("HEAD"), cancellable = true)
    private void gemini$executeCommand(
            ParseResults<CommandSourceStack> parseResults,
            String command,
            CallbackInfo callback
    ) {
        CommandSourceStack source = parseResults.getContext().getSource();
        CommandEvent event = new CommandEvent(source, command, CommandEvent.Phase.EXECUTE, 0);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}

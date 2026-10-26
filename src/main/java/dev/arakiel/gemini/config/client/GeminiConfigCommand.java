package dev.arakiel.gemini.config.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.arakiel.gemini.config.ConfigHandle;
import dev.arakiel.gemini.config.GeminiConfig;
import java.util.List;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public final class GeminiConfigCommand {
    private GeminiConfigCommand() {
    }

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registries) ->
                dispatcher.register(ClientCommandManager.literal("geminiconfig")
                        .executes(context -> {
                            context.getSource().sendError(
                                    Component.translatableWithFallback(
                                            "gemini.config.usage",
                                            "Usage: /geminiconfig <modid>"
                                    )
                            );
                            return 0;
                        })
                        .then(ClientCommandManager.argument("modid", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    SharedSuggestionProvider.suggest(
                                            GeminiConfig.all().stream()
                                                    .map(handle -> handle.id().getNamespace())
                                                    .distinct()
                                                    .toList(),
                                            builder
                                    );
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String namespace = StringArgumentType.getString(context, "modid");
                                    return openNamespace(context.getSource(), namespace);
                                })))
        );
    }

    private static int openNamespace(
            net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource source,
            String namespace
    ) {
        List<ConfigHandle<?>> matches = GeminiConfig.all().stream()
                .filter(handle -> handle.id().getNamespace().equals(namespace))
                .toList();
        if (matches.isEmpty()) {
            source.sendError(Component.translatableWithFallback(
                    "gemini.config.none_for_mod",
                    "No Gemini config is registered for %s",
                    namespace
            ));
            return 0;
        }
        if (matches.size() == 1) {
            GeminiConfigClient.open(null, matches.getFirst());
        } else {
            GeminiConfigClient.openHub(null, matches);
        }
        return matches.size();
    }
}

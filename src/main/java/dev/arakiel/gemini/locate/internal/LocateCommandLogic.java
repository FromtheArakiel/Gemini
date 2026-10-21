package dev.arakiel.gemini.locate.internal;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.locate.GeminiLocate;
import dev.arakiel.gemini.mixin.locate.LocateCommandAccessor;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class LocateCommandLogic {
    private LocateCommandLogic() {
    }

    public static void locateStructureAsync(
            CommandSourceStack source,
            ResourceOrTagKeyArgument.Result<Structure> result
    ) throws CommandSyntaxException {
        Registry<Structure> registry = source.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE);
        var key = result.unwrap();
        Optional<HolderSet<Structure>> holders = key.left()
                .flatMap(registry::getHolder)
                .<HolderSet<Structure>>map(HolderSet::direct)
                .or(() -> key.right().flatMap(registry::getTag));
        if (holders.isEmpty()) {
            source.sendFailure(Component.literal(LocateCommandAccessor.gemini$invalidStructure()
                    .create(result.asPrintable())
                    .getRawMessage()
                    .getString()));
            return;
        }

        BlockPos origin = BlockPos.containing(source.getPosition());
        Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
        GeminiLocate.findStructure(source.getLevel(), holders.get(), origin, 100, false)
                .withTimeout(GeminiLocate.config().mapTimeoutSeconds, TimeUnit.SECONDS)
                .handleOnServerThread((pair, throwable) -> {
                    if (!GeminiLocate.isLevelActive(source.getLevel())) {
                        return;
                    }
                    stopwatch.stop();
                    if (throwable != null) {
                        Gemini.LOGGER.error("/locate structure failed for {}", result.asPrintable(), throwable);
                        source.sendFailure(Component.literal(
                                "An unexpected error occurred while locating " + result.asPrintable()
                        ));
                    } else if (pair == null) {
                        source.sendFailure(Component.literal(LocateCommandAccessor.gemini$structureNotFound()
                                .create(result.asPrintable())
                                .getRawMessage()
                                .getString()));
                    } else {
                        LocateCommand.showLocateResult(
                                source,
                                result,
                                origin,
                                pair,
                                "commands.locate.structure.success",
                                false,
                                stopwatch.elapsed()
                        );
                    }
                });
    }

    public static void locateBiomeAsync(
            CommandSourceStack source,
            ResourceOrTagArgument.Result<Biome> result
    ) {
        BlockPos origin = BlockPos.containing(source.getPosition());
        Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
        GeminiLocate.findBiome(
                        source.getLevel(),
                        result,
                        origin,
                        GeminiLocate.config().biomeRadius,
                        GeminiLocate.config().biomeHorizontalStep,
                        GeminiLocate.config().biomeVerticalStep
                )
                .withTimeout(GeminiLocate.config().mapTimeoutSeconds, TimeUnit.SECONDS)
                .handleOnServerThread((pair, throwable) -> {
                    if (!GeminiLocate.isLevelActive(source.getLevel())) {
                        return;
                    }
                    stopwatch.stop();
                    if (throwable != null) {
                        Gemini.LOGGER.error("/locate biome failed for {}", result.asPrintable(), throwable);
                        source.sendFailure(Component.literal(
                                "An unexpected error occurred while locating " + result.asPrintable()
                        ));
                    } else if (pair == null) {
                        source.sendFailure(Component.literal(LocateCommandAccessor.gemini$biomeNotFound()
                                .create(result.asPrintable())
                                .getRawMessage()
                                .getString()));
                    } else {
                        LocateCommand.showLocateResult(
                                source,
                                result,
                                origin,
                                pair,
                                "commands.locate.biome.success",
                                true,
                                stopwatch.elapsed()
                        );
                    }
                });
    }
}

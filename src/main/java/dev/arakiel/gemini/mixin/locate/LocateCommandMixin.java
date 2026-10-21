package dev.arakiel.gemini.mixin.locate;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.arakiel.gemini.locate.GeminiLocate;
import dev.arakiel.gemini.locate.internal.LocateCommandLogic;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocateCommand.class)
public abstract class LocateCommandMixin {
    @Inject(method = "locateStructure", at = @At("HEAD"), cancellable = true)
    private static void gemini$locateStructureAsync(
            CommandSourceStack source,
            ResourceOrTagKeyArgument.Result<Structure> result,
            CallbackInfoReturnable<Integer> callback
    ) throws CommandSyntaxException {
        if (GeminiLocate.config().structureCommand) {
            LocateCommandLogic.locateStructureAsync(source, result);
            callback.setReturnValue(1);
        }
    }

    @Inject(method = "locateBiome", at = @At("HEAD"), cancellable = true)
    private static void gemini$locateBiomeAsync(
            CommandSourceStack source,
            ResourceOrTagArgument.Result<Biome> result,
            CallbackInfoReturnable<Integer> callback
    ) {
        if (GeminiLocate.config().biomeCommand) {
            LocateCommandLogic.locateBiomeAsync(source, result);
            callback.setReturnValue(1);
        }
    }
}

package dev.arakiel.gemini.mixin.locate;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.server.commands.LocateCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LocateCommand.class)
public interface LocateCommandAccessor {
    @Accessor("ERROR_STRUCTURE_INVALID")
    static DynamicCommandExceptionType gemini$invalidStructure() {
        throw new UnsupportedOperationException();
    }

    @Accessor("ERROR_STRUCTURE_NOT_FOUND")
    static DynamicCommandExceptionType gemini$structureNotFound() {
        throw new UnsupportedOperationException();
    }

    @Accessor("ERROR_BIOME_NOT_FOUND")
    static DynamicCommandExceptionType gemini$biomeNotFound() {
        throw new UnsupportedOperationException();
    }
}

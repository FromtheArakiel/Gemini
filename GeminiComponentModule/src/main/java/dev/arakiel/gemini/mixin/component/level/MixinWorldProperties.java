package dev.arakiel.gemini.mixin.component.level;

import dev.arakiel.gemini.component.ComponentProvider;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({LevelData.class})
public interface MixinWorldProperties extends ComponentProvider {
}

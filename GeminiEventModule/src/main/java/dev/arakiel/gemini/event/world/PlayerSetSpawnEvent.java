package dev.arakiel.gemini.event.world;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.player.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Cancelable
public class PlayerSetSpawnEvent extends PlayerEvent {
    private final ResourceKey<Level> dimension;
    private final @Nullable BlockPos pos;
    private final boolean forced;

    public PlayerSetSpawnEvent(Player player, ResourceKey<Level> dimension, @Nullable BlockPos pos, boolean forced) {
        super(player);
        this.dimension = Objects.requireNonNull(dimension, "dimension");
        this.pos = pos;
        this.forced = forced;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public @Nullable BlockPos getPos() {
        return this.pos;
    }

    public boolean isForced() {
        return this.forced;
    }
}

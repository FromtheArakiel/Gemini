package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.api.Cancelable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

@Cancelable
public class PlayerBreakBlockEvent extends PlayerEvent {
    private final BlockPos pos;
    private final BlockState state;

    public PlayerBreakBlockEvent(Player player, BlockPos pos, BlockState state) {
        super(player);
        this.pos = Objects.requireNonNull(pos, "pos");
        this.state = Objects.requireNonNull(state, "state");
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }
}

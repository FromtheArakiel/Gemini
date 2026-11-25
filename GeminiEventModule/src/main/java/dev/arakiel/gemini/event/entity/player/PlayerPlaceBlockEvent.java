package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

@Cancelable
public class PlayerPlaceBlockEvent extends PlayerEvent {
    private final BlockPos pos;
    private final BlockState state;
    private final ItemStack stack;

    public PlayerPlaceBlockEvent(Player player, BlockPos pos, BlockState state, ItemStack stack) {
        super(player);
        this.pos = Objects.requireNonNull(pos, "pos");
        this.state = Objects.requireNonNull(state, "state");
        this.stack = Objects.requireNonNull(stack, "stack");
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    public ItemStack getStack() {
        return stack;
    }
}

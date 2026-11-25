package dev.arakiel.gemini.event.level;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.block.BlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

@Cancelable
public class BonemealEvent extends BlockEvent {
    private final ItemStack stack;

    public BonemealEvent(Level level, BlockPos pos, BlockState state, ItemStack stack) {
        super(level, pos, state);
        this.stack = Objects.requireNonNull(stack, "stack");
    }

    public ItemStack getStack() {
        return stack;
    }
}

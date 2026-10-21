package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public interface PlacementBlockMethod extends MultipleBlockMethod {
   BlockState getStateForPlacement(BlockState var1, BlockPlaceContext var2);
}

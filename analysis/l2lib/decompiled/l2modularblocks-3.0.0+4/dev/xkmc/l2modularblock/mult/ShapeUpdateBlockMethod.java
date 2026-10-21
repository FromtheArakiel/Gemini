package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ShapeUpdateBlockMethod extends MultipleBlockMethod {
   BlockState updateShape(Block var1, BlockState var2, BlockState var3, Direction var4, BlockState var5, LevelAccessor var6, BlockPos var7, BlockPos var8);
}

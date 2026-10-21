package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface SurviveBlockMethod extends MultipleBlockMethod {
   boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3);
}

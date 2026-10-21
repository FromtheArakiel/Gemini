package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface OnReplacedBlockMethod extends MultipleBlockMethod {
   void onReplaced(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5);
}

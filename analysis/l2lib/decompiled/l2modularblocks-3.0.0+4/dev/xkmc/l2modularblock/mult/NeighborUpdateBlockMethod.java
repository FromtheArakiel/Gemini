package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface NeighborUpdateBlockMethod extends MultipleBlockMethod {
   void neighborChanged(Block var1, BlockState var2, Level var3, BlockPos var4, Block var5, BlockPos var6, boolean var7);
}

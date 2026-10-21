package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface LightBlockMethod extends SingletonBlockMethod {
   int getLightValue(BlockState var1, BlockGetter var2, BlockPos var3);
}

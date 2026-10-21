package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface AnalogOutputBlockMethod extends SingletonBlockMethod {
   int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3);

   default boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }
}

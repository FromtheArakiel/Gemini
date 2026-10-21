package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface EntityInsideBlockMethod extends SingletonBlockMethod {
   void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4);
}

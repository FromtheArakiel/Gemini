package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface FallOnBlockMethod extends MultipleBlockMethod {
   boolean fallOn(Level var1, BlockState var2, BlockPos var3, Entity var4, float var5);
}

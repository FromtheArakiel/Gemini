package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface StepOnBlockMethod extends MultipleBlockMethod {
   void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4);
}

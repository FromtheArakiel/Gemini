package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface GetBlockItemBlockMethod extends SingletonBlockMethod {
   ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3);
}

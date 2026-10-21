package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.world.level.block.state.BlockState;

public interface DefaultStateBlockMethod extends MultipleBlockMethod {
   BlockState getDefaultState(BlockState var1);
}

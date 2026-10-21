package dev.xkmc.l2modularblock.core;

import dev.xkmc.l2modularblock.type.BlockMethod;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class DelegateEntityBlockSimpleWaterImpl extends DelegateEntityBlockImpl implements SimpleWaterloggedBlock {
   protected DelegateEntityBlockSimpleWaterImpl(Properties p, BlockMethod... impl) {
      super(p, impl);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
   }
}

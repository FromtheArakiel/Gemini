package dev.xkmc.l2modularblock.impl;

import dev.xkmc.l2modularblock.mult.CreateBlockStateBlockMethod;
import dev.xkmc.l2modularblock.mult.DefaultStateBlockMethod;
import dev.xkmc.l2modularblock.mult.NeighborUpdateBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class TriggerBlockMethodImpl implements NeighborUpdateBlockMethod, CreateBlockStateBlockMethod, DefaultStateBlockMethod {
   private final int delay;

   public TriggerBlockMethodImpl(int delay) {
      this.delay = delay;
   }

   @Override
   public void neighborChanged(Block self, BlockState state, Level world, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving) {
      boolean flag = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
      boolean flag1 = (Boolean)state.getValue(BlockStateProperties.TRIGGERED);
      if (flag && !flag1) {
         world.scheduleTick(pos, self, this.delay);
         world.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.TRIGGERED, Boolean.TRUE), this.delay);
      } else if (!flag && flag1) {
         world.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.TRIGGERED, Boolean.FALSE), this.delay);
      }
   }

   @Override
   public void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{BlockStateProperties.TRIGGERED});
   }

   @Override
   public BlockState getDefaultState(BlockState state) {
      return (BlockState)state.setValue(BlockStateProperties.TRIGGERED, false);
   }
}

package dev.xkmc.l2modularblock.impl;

import dev.xkmc.l2modularblock.mult.CreateBlockStateBlockMethod;
import dev.xkmc.l2modularblock.mult.DefaultStateBlockMethod;
import dev.xkmc.l2modularblock.one.BlockPowerBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class PowerBlockMethodImpl implements CreateBlockStateBlockMethod, BlockPowerBlockMethod, DefaultStateBlockMethod {
   @Override
   public void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{BlockStateProperties.POWER});
   }

   @Override
   public int getSignal(BlockState bs, BlockGetter r, BlockPos pos, Direction d) {
      return (Integer)bs.getValue(BlockStateProperties.POWER);
   }

   @Override
   public BlockState getDefaultState(BlockState state) {
      return (BlockState)state.setValue(BlockStateProperties.POWER, 0);
   }
}

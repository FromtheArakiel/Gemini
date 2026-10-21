package dev.xkmc.l2modularblock.impl;

import dev.xkmc.l2modularblock.mult.CreateBlockStateBlockMethod;
import dev.xkmc.l2modularblock.mult.DefaultStateBlockMethod;
import dev.xkmc.l2modularblock.mult.PlacementBlockMethod;
import dev.xkmc.l2modularblock.mult.ShapeUpdateBlockMethod;
import dev.xkmc.l2modularblock.type.WaterloggedMethodMarker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public record SimpleWaterloggedImpl()
   implements CreateBlockStateBlockMethod,
   DefaultStateBlockMethod,
   PlacementBlockMethod,
   ShapeUpdateBlockMethod,
   WaterloggedMethodMarker {
   @Override
   public void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{BlockStateProperties.WATERLOGGED});
   }

   @Override
   public BlockState getDefaultState(BlockState state) {
      return (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, false);
   }

   @Override
   public BlockState getStateForPlacement(BlockState def, BlockPlaceContext ctx) {
      FluidState fluid = ctx.getLevel().getFluidState(ctx.getClickedPos());
      return (BlockState)def.setValue(BlockStateProperties.WATERLOGGED, fluid.is(Fluids.WATER));
   }

   @Override
   public BlockState updateShape(
      Block self, BlockState selfCurrent, BlockState selfOld, Direction from, BlockState sourceState, LevelAccessor level, BlockPos selfPos, BlockPos sourcePos
   ) {
      if ((Boolean)selfOld.getValue(BlockStateProperties.WATERLOGGED)) {
         level.scheduleTick(selfPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return selfCurrent;
   }
}

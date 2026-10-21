package dev.xkmc.l2modularblock.impl;

import dev.xkmc.l2modularblock.core.BlockTemplates;
import dev.xkmc.l2modularblock.mult.CreateBlockStateBlockMethod;
import dev.xkmc.l2modularblock.mult.PlacementBlockMethod;
import dev.xkmc.l2modularblock.one.MirrorRotateBlockMethod;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;

public class AllDireBlockMethodImpl implements PlacementBlockMethod, CreateBlockStateBlockMethod, MirrorRotateBlockMethod {
   @Override
   public void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{BlockTemplates.FACING});
   }

   @Override
   public BlockState getStateForPlacement(BlockState def, BlockPlaceContext context) {
      return (BlockState)def.setValue(BlockTemplates.FACING, context.getClickedFace().getOpposite());
   }

   @Override
   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(BlockTemplates.FACING, rot.rotate((Direction)state.getValue(BlockTemplates.FACING)));
   }

   @Override
   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(BlockTemplates.FACING)));
   }
}

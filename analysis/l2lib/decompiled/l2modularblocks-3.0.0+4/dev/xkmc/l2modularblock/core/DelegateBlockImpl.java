package dev.xkmc.l2modularblock.core;

import dev.xkmc.l2modularblock.mult.AnimateTickBlockMethod;
import dev.xkmc.l2modularblock.mult.AttackBlockMethod;
import dev.xkmc.l2modularblock.mult.CreateBlockStateBlockMethod;
import dev.xkmc.l2modularblock.mult.DefaultStateBlockMethod;
import dev.xkmc.l2modularblock.mult.FallOnBlockMethod;
import dev.xkmc.l2modularblock.mult.NeighborUpdateBlockMethod;
import dev.xkmc.l2modularblock.mult.OnPlaceBlockMethod;
import dev.xkmc.l2modularblock.mult.OnReplacedBlockMethod;
import dev.xkmc.l2modularblock.mult.PlacementBlockMethod;
import dev.xkmc.l2modularblock.mult.RandomTickBlockMethod;
import dev.xkmc.l2modularblock.mult.ScheduleTickBlockMethod;
import dev.xkmc.l2modularblock.mult.SetPlacedByBlockMethod;
import dev.xkmc.l2modularblock.mult.ShapeUpdateBlockMethod;
import dev.xkmc.l2modularblock.mult.StepOnBlockMethod;
import dev.xkmc.l2modularblock.mult.SurviveBlockMethod;
import dev.xkmc.l2modularblock.mult.ToolTipBlockMethod;
import dev.xkmc.l2modularblock.mult.UseItemOnBlockMethod;
import dev.xkmc.l2modularblock.mult.UseWithoutItemBlockMethod;
import dev.xkmc.l2modularblock.one.AnalogOutputBlockMethod;
import dev.xkmc.l2modularblock.one.BlockEntityBlockMethod;
import dev.xkmc.l2modularblock.one.BlockPowerBlockMethod;
import dev.xkmc.l2modularblock.one.EntityInsideBlockMethod;
import dev.xkmc.l2modularblock.one.GetBlockItemBlockMethod;
import dev.xkmc.l2modularblock.one.LightBlockMethod;
import dev.xkmc.l2modularblock.one.MirrorRotateBlockMethod;
import dev.xkmc.l2modularblock.one.RenderShapeBlockMethod;
import dev.xkmc.l2modularblock.one.ShapeBlockMethod;
import dev.xkmc.l2modularblock.one.SpecialDropBlockMethod;
import dev.xkmc.l2modularblock.tile_api.BlockContainer;
import dev.xkmc.l2modularblock.type.BlockMethod;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DelegateBlockImpl extends DelegateBlock {
   private static final ThreadLocal<BlockImplementor> TEMP = new ThreadLocal<>();
   BlockImplementor impl;

   protected DelegateBlockImpl(Properties p, BlockMethod... impl) {
      super(handler(new BlockImplementor(p).addImpls(impl)));
      this.registerDefaultState(this.impl.reduce(DefaultStateBlockMethod.class, this.defaultBlockState(), (state, def) -> def.getDefaultState(state)));
   }

   private static Properties handler(BlockImplementor bi) {
      if (TEMP.get() != null) {
         throw new RuntimeException("concurrency error");
      }

      TEMP.set(bi);
      return bi.props;
   }

   public final boolean isSignalSource(BlockState bs) {
      return this.impl.one(BlockPowerBlockMethod.class).isPresent();
   }

   public final int getAnalogOutputSignal(BlockState blockState, Level levelIn, BlockPos pos) {
      return this.impl.one(AnalogOutputBlockMethod.class).map(e -> e.getAnalogOutputSignal(blockState, levelIn, pos)).orElse(0);
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return this.impl.one(AnalogOutputBlockMethod.class).map(e -> e.hasAnalogOutputSignal(state)).orElse(false);
   }

   public final int getLightEmission(BlockState bs, BlockGetter w, BlockPos pos) {
      return this.impl.one(LightBlockMethod.class).map(e -> e.getLightValue(bs, w, pos)).orElse(super.getLightEmission(bs, w, pos));
   }

   public final BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.impl.reduce(PlacementBlockMethod.class, this.defaultBlockState(), (state, impl) -> impl.getStateForPlacement(state, context));
   }

   public final int getSignal(BlockState bs, BlockGetter r, BlockPos pos, Direction d) {
      return this.impl.one(BlockPowerBlockMethod.class).map(e -> e.getSignal(bs, r, pos, d)).orElse(0);
   }

   public final BlockState mirror(BlockState state, Mirror mirrorIn) {
      return this.impl.one(MirrorRotateBlockMethod.class).map(e -> e.mirror(state, mirrorIn)).orElse(state);
   }

   protected InteractionResult useWithoutItem(BlockState bs, Level w, BlockPos pos, Player pl, BlockHitResult hit) {
      return this.impl
         .execute(UseWithoutItemBlockMethod.class)
         .map(e -> e.useWithoutItem(bs, w, pos, pl, hit))
         .filter(e -> e != InteractionResult.PASS)
         .findFirst()
         .orElse(InteractionResult.PASS);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      return this.impl
         .execute(UseItemOnBlockMethod.class)
         .map(e -> e.useItemOn(stack, state, level, pos, player, hand, hit))
         .filter(e -> e != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)
         .findFirst()
         .orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
   }

   public final void onRemove(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving) {
      this.impl.forEach(OnReplacedBlockMethod.class, e -> e.onReplaced(state, levelIn, pos, newState, isMoving));
      if (this.impl.one(BlockEntityBlockMethod.class).isPresent() && state.getBlock() != newState.getBlock()) {
         BlockEntity entity = levelIn.getBlockEntity(pos);
         if (entity != null) {
            if (entity instanceof Container) {
               Containers.dropContents(levelIn, pos, (Container)entity);
               levelIn.updateNeighbourForOutputSignal(pos, this);
            } else if (entity instanceof BlockContainer blockContainer) {
               for (Container c : blockContainer.getContainers()) {
                  Containers.dropContents(levelIn, pos, c);
               }

               levelIn.updateNeighbourForOutputSignal(pos, this);
            }

            levelIn.removeBlockEntity(pos);
         }
      }
   }

   public final BlockState rotate(BlockState state, Rotation rot) {
      return this.impl.one(MirrorRotateBlockMethod.class).map(e -> e.rotate(state, rot)).orElse(state);
   }

   protected final void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      this.impl = TEMP.get();
      TEMP.set(null);
      this.impl.forEach(CreateBlockStateBlockMethod.class, e -> e.createBlockStateDefinition(builder));
   }

   public final void neighborChanged(BlockState state, Level level, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving) {
      this.impl.forEach(NeighborUpdateBlockMethod.class, e -> e.neighborChanged(this, state, level, pos, nei_block, nei_pos, moving));
      super.neighborChanged(state, level, pos, nei_block, nei_pos, moving);
   }

   public final void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      this.impl.forEach(RandomTickBlockMethod.class, e -> e.randomTick(state, level, pos, random));
   }

   public final void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      this.impl.forEach(ScheduleTickBlockMethod.class, e -> e.tick(state, level, pos, random));
   }

   @OnlyIn(Dist.CLIENT)
   public final void animateTick(BlockState state, Level level, BlockPos pos, RandomSource r) {
      this.impl.forEach(AnimateTickBlockMethod.class, e -> e.animateTick(state, level, pos, r));
   }

   public final void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      this.impl.one(EntityInsideBlockMethod.class).ifPresent(e -> e.entityInside(state, level, pos, entity));
   }

   public final VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return !this.hasCollision
         ? Shapes.empty()
         : this.impl
            .one(ShapeBlockMethod.class)
            .map(e -> e.getCollisionShape(state, level, pos, ctx))
            .orElseGet(() -> super.getCollisionShape(state, level, pos, ctx));
   }

   public final VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
      return this.impl
         .one(ShapeBlockMethod.class)
         .map(e -> e.getBlockSupportShape(state, level, pos))
         .orElseGet(() -> super.getBlockSupportShape(state, level, pos));
   }

   public final VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return this.impl
         .one(ShapeBlockMethod.class)
         .map(e -> e.getVisualShape(state, level, pos, ctx))
         .orElseGet(() -> super.getVisualShape(state, level, pos, ctx));
   }

   public final VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return this.impl.one(ShapeBlockMethod.class).map(e -> e.getShape(state, level, pos, ctx)).orElseGet(() -> super.getShape(state, level, pos, ctx));
   }

   public final void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float height) {
      if (this.impl.reduce(FallOnBlockMethod.class, true, (a, e) -> a & e.fallOn(level, state, pos, entity, height))) {
         super.fallOn(level, state, pos, entity, height);
      }
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return this.impl.one(GetBlockItemBlockMethod.class).map(e -> e.getCloneItemStack(level, pos, state)).orElse(super.getCloneItemStack(level, pos, state));
   }

   public final List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      return this.impl.one(SpecialDropBlockMethod.class).map(e -> e.getDrops(state, builder)).orElse(super.getDrops(state, builder));
   }

   public final void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
      this.impl.forEach(SetPlacedByBlockMethod.class, e -> e.setPlacedBy(level, pos, state, entity, stack));
   }

   @OnlyIn(Dist.CLIENT)
   public final RenderShape getRenderShape(BlockState state) {
      return this.impl.one(RenderShapeBlockMethod.class).map(e -> e.getRenderShape(state)).orElseGet(() -> super.getRenderShape(state));
   }

   public final void attack(BlockState state, Level level, BlockPos pos, Player player) {
      this.impl.execute(AttackBlockMethod.class).filter(u -> u.attack(state, level, pos, player)).findFirst();
   }

   public final BlockState updateShape(BlockState selfState, Direction from, BlockState sourceState, LevelAccessor level, BlockPos selfPos, BlockPos sourcePos) {
      return this.impl
         .reduce(
            ShapeUpdateBlockMethod.class,
            selfState,
            (currentState, e) -> e.updateShape(this, currentState, selfState, from, sourceState, level, selfPos, sourcePos)
         );
   }

   public final void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
      this.impl.forEach(ToolTipBlockMethod.class, e -> e.appendHoverText(stack, ctx, list, flag));
   }

   public final void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
      this.impl.forEach(OnPlaceBlockMethod.class, e -> e.onPlace(state, level, pos, old, moving));
   }

   public final void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
      this.impl.forEach(StepOnBlockMethod.class, e -> e.stepOn(pLevel, pPos, pState, pEntity));
   }

   protected final boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      return this.impl.testAnd(SurviveBlockMethod.class, e -> e.canSurvive(state, level, pos));
   }

   public final BlockImplementor getImpl() {
      return this.impl;
   }
}

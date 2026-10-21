package com.tterrag.registrate.builders;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockEntityBuilder<T extends BlockEntity, P> extends AbstractBuilder<BlockEntityType<?>, BlockEntityType<T>, P, BlockEntityBuilder<T, P>> {
   private final BlockEntityBuilder.BlockEntityFactory<T> factory;
   private final Set<NonNullSupplier<? extends Block>> validBlocks = new HashSet<>();
   @Nullable
   private NonNullSupplier<NonNullFunction<Context, BlockEntityRenderer<? super T>>> renderer;

   public static <T extends BlockEntity, P> BlockEntityBuilder<T, P> create(
      AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityBuilder.BlockEntityFactory<T> factory
   ) {
      return new BlockEntityBuilder<>(owner, parent, name, callback, factory);
   }

   protected BlockEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityBuilder.BlockEntityFactory<T> factory) {
      super(owner, parent, name, callback, Registries.BLOCK_ENTITY_TYPE);
      this.factory = factory;
   }

   public BlockEntityBuilder<T, P> validBlock(NonNullSupplier<? extends Block> block) {
      this.validBlocks.add(block);
      return this;
   }

   @SafeVarargs
   public final BlockEntityBuilder<T, P> validBlocks(NonNullSupplier<? extends Block>... blocks) {
      Arrays.stream(blocks).forEach(this::validBlock);
      return this;
   }

   public BlockEntityBuilder<T, P> renderer(NonNullSupplier<NonNullFunction<Context, BlockEntityRenderer<? super T>>> renderer) {
      if (this.renderer == null) {
         RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::registerRenderer);
      }

      this.renderer = renderer;
      return this;
   }

   protected void registerRenderer() {
      OneTimeEventReceiver.addModListener(this.getOwner(), FMLClientSetupEvent.class, $ -> {
         NonNullSupplier<NonNullFunction<Context, BlockEntityRenderer<? super T>>> renderer = this.renderer;
         if (renderer != null) {
            BlockEntityRenderers.register(this.getEntry(), renderer.get()::apply);
         }
      });
   }

   public BlockEntityBuilder<T, P> registerCapability(Consumer<RegisterCapabilitiesEvent> registerCapabilitiesEvent) {
      OneTimeEventReceiver.addModListener(this.getOwner(), RegisterCapabilitiesEvent.class, registerCapabilitiesEvent);
      return this;
   }

   protected BlockEntityType<T> createEntry() {
      BlockEntityBuilder.BlockEntityFactory<T> factory = this.factory;
      NonNullSupplier<BlockEntityType<T>> supplier = this.asSupplier();
      return net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
            (pos, state) -> factory.create(supplier.get(), pos, state), this.validBlocks.stream().map(NonNullSupplier::get).toArray(Block[]::new)
         )
         .build(null);
   }

   @Override
   protected RegistryEntry<BlockEntityType<?>, BlockEntityType<T>> createEntryWrapper(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> delegate) {
      return new BlockEntityEntry<>(this.getOwner(), delegate);
   }

   public BlockEntityEntry<T> register() {
      return (BlockEntityEntry<T>)super.register();
   }

   public interface BlockEntityFactory<T extends BlockEntity> {
      T create(BlockEntityType<T> var1, BlockPos var2, BlockState var3);
   }
}

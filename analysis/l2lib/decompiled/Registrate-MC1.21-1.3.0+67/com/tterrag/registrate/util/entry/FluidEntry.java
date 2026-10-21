package com.tterrag.registrate.util.entry;

import com.tterrag.registrate.AbstractRegistrate;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FluidEntry<T extends BaseFlowingFluid> extends RegistryEntry<Fluid, T> {
   @Nullable
   private final BlockEntry<? extends Block> block;

   public FluidEntry(AbstractRegistrate<?> owner, DeferredHolder<Fluid, T> delegate) {
      super(owner, delegate);
      BlockEntry<? extends Block> block = null;

      try {
         block = BlockEntry.cast(this.getSibling(BuiltInRegistries.BLOCK));
      } catch (IllegalArgumentException var5) {
      }

      this.block = block;
   }

   @Override
   public <R> boolean is(R entry) {
      return this.get().isSame((Fluid)entry);
   }

   public <S extends BaseFlowingFluid> S getSource() {
      return (S)this.get().getSource();
   }

   public FluidType getType() {
      return this.get().getFluidType();
   }

   public <B extends Block> Optional<B> getBlock() {
      return Optional.ofNullable(this.block).map(DeferredHolder::get);
   }

   public <I extends Item> Optional<I> getBucket() {
      return (Optional<I>)Optional.ofNullable(this.get().getBucket());
   }
}

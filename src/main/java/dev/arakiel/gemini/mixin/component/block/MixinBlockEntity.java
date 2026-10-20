package dev.arakiel.gemini.mixin.component.block;

import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.sync.AutoSyncedComponent;
import dev.arakiel.gemini.component.internal.block.BlockEntityAddress;
import dev.arakiel.gemini.component.internal.block.GeminiBlockComponents;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.block.GeminiBlockInternals;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockEntity.class})
public abstract class MixinBlockEntity implements ComponentProvider {
   @Unique
   private ComponentContainer components;

   @Shadow
   @Nullable
   public abstract Level getLevel();

   @Shadow
   public abstract BlockPos getBlockPos();

   @Shadow
   public abstract BlockEntityType<?> getType();

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void init(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
      this.components = GeminiBlockInternals.createComponents((BlockEntity)(Object)this);
   }

   @Inject(
      method = {"saveWithoutMetadata"},
      at = {@At("RETURN")}
   )
   private void writeNbt(Provider registryLookup, CallbackInfoReturnable<CompoundTag> cir) {
      this.components.toTag((CompoundTag)cir.getReturnValue(), registryLookup);
   }

   @Inject(
      method = {"loadWithComponents"},
      at = {@At("RETURN")}
   )
   private void readNbt(CompoundTag nbt, Provider registryLookup, CallbackInfo ci) {
      this.components.fromTag(nbt, registryLookup);
   }

   @Nonnull
   @Override
   public ComponentContainer getComponentContainer() {
      return this.components;
   }

   @Override
   public Iterable<ServerPlayer> getRecipientsForComponentSync() {
      Level world = this.getLevel();
      return (Iterable<ServerPlayer>)(world != null && !world.isClientSide ? PlayerLookup.tracking((BlockEntity)(Object)this) : List.of());
   }

   public <C extends AutoSyncedComponent> ComponentUpdatePayload<?> toComponentPacket(
      ComponentKey<? super C> key, boolean required, RegistryFriendlyByteBuf data
   ) {
      return new ComponentUpdatePayload<>(
         GeminiBlockComponents.PACKET_ID, new BlockEntityAddress(this.getType(), this.getBlockPos()), required, key.getId(), data
      );
   }
}

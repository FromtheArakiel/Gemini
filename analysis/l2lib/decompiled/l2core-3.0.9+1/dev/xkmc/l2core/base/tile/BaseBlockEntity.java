package dev.xkmc.l2core.base.tile;

import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SerialClass
public class BaseBlockEntity extends BlockEntity {
   public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public void loadAdditional(CompoundTag tag, Provider pvd) {
      super.loadAdditional(tag, pvd);
      if (tag.contains("auto-serial")) {
         new TagCodec(pvd).fromTag(tag.getCompound("auto-serial"), this.getClass(), this);
      }
   }

   public void saveAdditional(CompoundTag tag, Provider pvd) {
      super.saveAdditional(tag, pvd);
      CompoundTag ser = new TagCodec(pvd).toTag(new CompoundTag(), this.getClass(), this);
      if (ser != null) {
         tag.put("auto-serial", ser);
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sync() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public CompoundTag getUpdateTag(Provider pvd) {
      CompoundTag ans = super.getUpdateTag(pvd);
      CompoundTag ser = new TagCodec(pvd).pred(SerialField::toClient).toTag(new CompoundTag(), this.getClass(), this);
      if (ser != null) {
         ans.put("auto-serial", ser);
      }

      return ans;
   }
}
